package com.takeout.merchant.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.merchant.dto.CartItemDTO;
import com.takeout.merchant.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "购物车", description = "购物车管理接口")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(HttpServletRequest request,
                                   @RequestParam Long dishId,
                                   @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = SecurityUtil.getUserId(request);
        cartService.addToCart(userId, dishId, quantity);
        return Result.success(null);
    }

    @DeleteMapping("/remove")
    public Result<Void> removeFromCart(HttpServletRequest request,
                                       @RequestParam Long dishId) {
        Long userId = SecurityUtil.getUserId(request);
        cartService.removeFromCart(userId, dishId);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result<Void> updateQuantity(HttpServletRequest request,
                                       @RequestParam Long dishId,
                                       @RequestParam Integer quantity) {
        Long userId = SecurityUtil.getUserId(request);
        cartService.updateQuantity(userId, dishId, quantity);
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart(HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId(request);
        cartService.clearCart(userId);
        return Result.success(null);
    }

    @GetMapping("/list")
    public Result<List<CartItemDTO>> getCart(HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId(request);
        List<CartItemDTO> cart = cartService.getCart(userId);
        return Result.success(cart);
    }

    @GetMapping("/checkout")
    public Result<Map<String, Object>> checkout(HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId(request);
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
