package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.takeout.merchant.dto.InventoryDTO;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.service.DishService;
import com.takeout.merchant.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final DishService dishService;
    private final StringRedisTemplate redisTemplate;

    private static final String DEDUCTED_ORDER_PREFIX = "inventory:deducted:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(InventoryDTO inventoryDTO) {
        Long dishId = inventoryDTO.getDishId();
        Integer quantity = inventoryDTO.getQuantity();
        String orderId = inventoryDTO.getOrderId();

        String deductedKey = DEDUCTED_ORDER_PREFIX + orderId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(deductedKey))) {
            log.info("订单 {} 已扣减过库存，幂等返回", orderId);
            return true;
        }

        Dish dish = dishService.getById(dishId);
        if (dish == null || dish.getStatus() != 1) {
            throw new RuntimeException("菜品不存在或已下架");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, dishId)
                     .eq(Dish::getVersion, dish.getVersion())
                     .ge(Dish::getStock, quantity);

        dish.setStock(dish.getStock() - quantity);
        dish.setVersion(dish.getVersion() + 1);

        boolean success = dishService.update(dish, updateWrapper);
        if (!success) {
            throw new RuntimeException("库存扣减失败，可能库存不足或并发冲突");
        }

        redisTemplate.opsForValue().set(deductedKey, "1", 24, TimeUnit.HOURS);

        log.info("订单 {} 扣减菜品 {} 库存成功，扣减数量 {}", orderId, dishId, quantity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revertStock(InventoryDTO inventoryDTO) {
        Long dishId = inventoryDTO.getDishId();
        Integer quantity = inventoryDTO.getQuantity();
        String orderId = inventoryDTO.getOrderId();

        Dish dish = dishService.getById(dishId);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, dishId)
                     .eq(Dish::getVersion, dish.getVersion());

        dish.setStock(dish.getStock() + quantity);
        dish.setVersion(dish.getVersion() + 1);

        boolean success = dishService.update(dish, updateWrapper);
        if (!success) {
            throw new RuntimeException("库存返还失败");
        }

        String deductedKey = DEDUCTED_ORDER_PREFIX + orderId;
        redisTemplate.delete(deductedKey);

        log.info("订单 {} 返还菜品 {} 库存成功，返还数量 {}", orderId, dishId, quantity);
        return true;
    }

    @Override
    public boolean checkStock(Long dishId, Integer quantity) {
        Dish dish = dishService.getById(dishId);
        if (dish == null || dish.getStatus() != 1) {
            return false;
        }
        return dish.getStock() >= quantity;
    }
}
