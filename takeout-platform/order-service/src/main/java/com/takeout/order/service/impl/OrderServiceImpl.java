package com.takeout.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.order.entity.Order;
import com.takeout.order.entity.OrderItem;
import com.takeout.order.mapper.OrderMapper;
import com.takeout.order.service.OrderItemService;
import com.takeout.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public Order createOrder(Long userId, Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException(400, "订单商品不能为空");
        }

        // Generate order number
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setStatus("PENDING");

        // Calculate totals
        int totalQty = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            totalQty += item.getQuantity();
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalQuantity(totalQty);
        if (order.getDeliveryFee() == null) {
            order.setDeliveryFee(BigDecimal.ZERO);
        }
        order.setTotalPrice(total.add(order.getDeliveryFee()));

        // Save order
        save(order);

        // Save items
        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
        }
        orderItemService.saveBatch(order.getItems());

        return order;
    }

    @Override
    public IPage<Order> listUserOrders(Long userId, Page<Order> page) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreatedAt);
        IPage<Order> result = page(page, wrapper);
        // Attach items to each order
        for (Order o : result.getRecords()) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, o.getId());
            o.setItems(orderItemService.list(itemWrapper));
        }
        return result;
    }

    @Override
    public Order getOrderDetail(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(404, "订单不存在");
        }
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        order.setItems(orderItemService.list(wrapper));
        return order;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "当前订单状态不可取消");
        }
        order.setStatus("CANCELLED");
        return updateById(order);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return timestamp + random;
    }
}
