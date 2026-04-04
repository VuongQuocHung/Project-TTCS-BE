package com.ttcs.backend.service;

import com.ttcs.backend.entity.Order;
import com.ttcs.backend.entity.OrderStatus;
import com.ttcs.backend.repository.OrderRepository;
import com.ttcs.backend.specification.OrderSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getFilteredOrders(OrderStatus status, Long userId, String phoneNumber, BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        Specification<Order> spec = Specification.where(OrderSpecs.withFetchUser())
                .and(OrderSpecs.hasStatus(status))
                .and(OrderSpecs.hasUserId(userId))
                .and(OrderSpecs.hasPhoneNumber(phoneNumber))
                .and(OrderSpecs.totalAmountBetween(minAmount, maxAmount));
        return orderRepository.findAll(spec, pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}
