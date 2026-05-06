package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.merchant.dto.CartItemDTO;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.service.CartService;
import com.takeout.merchant.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final DishService dishService;

    private static final Map<Long, Map<Long, Integer>> CART_CACHE = new ConcurrentHashMap<>();

    @Override
    public void addToCart(Long userId, Long dishId, Integer quantity) {
        Dish dish = dishService.getById(dishId);
        if (dish == null || dish.getStatus() != 1) {
            throw new RuntimeException("菜品不存在或已下架");
        }

        if (dish.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        Map<Long, Integer> userCart = CART_CACHE.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        Integer existingQty = userCart.get(dishId);
        if (existingQty != null) {
            quantity += existingQty;
        }

        userCart.put(dishId, quantity);
        log.info("用户 {} 添加菜品 {} 到购物车，数量 {}", userId, dishId, quantity);
    }

    @Override
    public void removeFromCart(Long userId, Long dishId) {
        Map<Long, Integer> userCart = CART_CACHE.get(userId);
        if (userCart != null) {
            userCart.remove(dishId);
        }
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

        Map<Long, Integer> userCart = CART_CACHE.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        userCart.put(dishId, quantity);
        log.info("用户 {} 更新购物车菜品 {} 数量为 {}", userId, dishId, quantity);
    }

    @Override
    public void clearCart(Long userId) {
        CART_CACHE.remove(userId);
        log.info("用户 {} 清空购物车", userId);
    }

    @Override
    public List<CartItemDTO> getCart(Long userId) {
        Map<Long, Integer> userCart = CART_CACHE.get(userId);
        if (userCart == null) {
            return new ArrayList<>();
        }

        List<CartItemDTO> result = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : userCart.entrySet()) {
            Long dishId = entry.getKey();
            Integer quantity = entry.getValue();

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