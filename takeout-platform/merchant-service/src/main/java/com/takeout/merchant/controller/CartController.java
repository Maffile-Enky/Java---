package com.takeout.merchant.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.merchant.dto.CartItemDTO;
import com.takeout.merchant.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestParam Long userId,
                                   @RequestParam Long dishId,
                                   @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addToCart(userId, dishId, quantity);
        return Result.success(null);
    }

    @DeleteMapping("/remove")
    public Result<Void> removeFromCart(@RequestParam Long userId,
                                       @RequestParam Long dishId) {
        cartService.removeFromCart(userId, dishId);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result<Void> updateQuantity(@RequestParam Long userId,
                                       @RequestParam Long dishId,
                                       @RequestParam Integer quantity) {
        cartService.updateQuantity(userId, dishId, quantity);
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return Result.success(null);
    }

    @GetMapping("/list")
    public Result<List<CartItemDTO>> getCart(@RequestParam Long userId) {
        List<CartItemDTO> cart = cartService.getCart(userId);
        return Result.success(cart);
    }

    @GetMapping("/checkout")
    public Result<Map<String, Object>> checkout(@RequestParam Long userId) {
        List<CartItemDTO> cart = cartService.getCart(userId);

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemDTO item : cart) {
            totalPrice = totalPrice.add(item.getSubtotal());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", cart);
        result.put("totalPrice", totalPrice);
        result.put("totalCount", cart.size());

        return Result.success(result);
    }
}