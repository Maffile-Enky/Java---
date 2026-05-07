package com.takeout.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.common.core.domain.Result;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.order.entity.Order;
import com.takeout.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result<Order> createOrder(HttpServletRequest request, @RequestBody Order order) {
        Long userId = SecurityUtil.getUserId(request);
        Order created = orderService.createOrder(userId, order);
        return Result.success(created);
    }

    @GetMapping("/list")
    public Result<IPage<Order>> list(HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getUserId(request);
        IPage<Order> result = orderService.listUserOrders(userId, new Page<>(page, size));
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Order> detail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = SecurityUtil.getUserId(request);
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long userId = SecurityUtil.getUserId(request);
        return Result.success(orderService.cancelOrder(userId, id));
    }
}
