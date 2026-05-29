package com.takeout.merchant.service.impl;

import com.takeout.merchant.dto.CartItemDTO;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.service.CartService;
import com.takeout.merchant.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final DishService dishService;
    private final StringRedisTemplate redisTemplate;

    // Redis Key 前缀
    private static final String CART_KEY_PREFIX = "cart:";

    @Override
    public void addToCart(Long userId, Long dishId, Integer quantity) {
        Dish dish = dishService.getById(dishId);
        if (dish == null || dish.getStatus() != 1) {
            throw new RuntimeException("菜品不存在或已下架");
        }

        if (dish.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        String cartKey = CART_KEY_PREFIX + userId;
        String field = dishId.toString();
        
        // 获取当前数量
        Object existingQtyObj = redisTemplate.opsForHash().get(cartKey, field);
        Integer existingQty = existingQtyObj != null ? Integer.parseInt(existingQtyObj.toString()) : 0;
        Integer newQty = existingQty + quantity;

        // 检查总数量是否超过库存
        if (dish.getStock() < newQty) {
            throw new RuntimeException("库存不足");
        }

        redisTemplate.opsForHash().put(cartKey, field, newQty.toString());
        log.info("用户 {} 添加菜品 {} 到购物车，数量 {}", userId, dishId, newQty);
    }

    @Override
    public void removeFromCart(Long userId, Long dishId) {
        String cartKey = CART_KEY_PREFIX + userId;
        redisTemplate.opsForHash().delete(cartKey, dishId.toString());
        log.info("用户 {} 从购物车删除菜品 {}", userId, dishId);
    }

    @Override
    public void updateQuantity(Long userId, Long dishId, Integer quantity) {
        if (quantity <= 0) {
            removeFromCart(userId, dishId);
            return;
        }

        Dish dish = dishService.getById(dishId);
        if (dish != null && dish.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        String cartKey = CART_KEY_PREFIX + userId;
        redisTemplate.opsForHash().put(cartKey, dishId.toString(), quantity.toString());
        log.info("用户 {} 更新购物车菜品 {} 数量为 {}", userId, dishId, quantity);
    }

    @Override
    public void clearCart(Long userId) {
        String cartKey = CART_KEY_PREFIX + userId;
        redisTemplate.delete(cartKey);
        log.info("用户 {} 清空购物车", userId);
    }

    @Override
    public List<CartItemDTO> getCart(Long userId) {
        String cartKey = CART_KEY_PREFIX + userId;
        Map<Object, Object> cartMap = redisTemplate.opsForHash().entries(cartKey);
        
        if (cartMap == null || cartMap.isEmpty()) {
            return new ArrayList<>();
        }

        List<CartItemDTO> result = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            Long dishId = Long.parseLong(key.toString());
            Integer quantity = Integer.parseInt(value.toString());

            Dish dish = dishService.getById(dishId);
            if (dish != null) {
                CartItemDTO item = new CartItemDTO();
                item.setDishId(dishId);
                item.setDishName(dish.getName());
                item.setPrice(dish.getPrice());
                item.setQuantity(quantity);
                item.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(quantity)));
                result.add(item);
            }
        }

        return result;
    }
}