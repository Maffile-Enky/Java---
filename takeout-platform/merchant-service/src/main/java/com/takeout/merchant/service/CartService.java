package com.takeout.merchant.service;

import com.takeout.merchant.dto.CartItemDTO;

import java.util.List;

public interface CartService {

    void addToCart(Long userId, Long dishId, Integer quantity);

    void removeFromCart(Long userId, Long dishId);

    void updateQuantity(Long userId, Long dishId, Integer quantity);

    void clearCart(Long userId);

    List<CartItemDTO> getCart(Long userId);
}