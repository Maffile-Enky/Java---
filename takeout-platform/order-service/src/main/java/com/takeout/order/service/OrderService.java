package com.takeout.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.order.entity.Order;

public interface OrderService extends IService<Order> {
    Order createOrder(Long userId, Order order);
    IPage<Order> listUserOrders(Long userId, Page<Order> page);
    Order getOrderDetail(Long userId, Long orderId);
    boolean cancelOrder(Long userId, Long orderId);
}
