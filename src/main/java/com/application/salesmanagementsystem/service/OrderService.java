package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Integer id);
    Order createOrder(Order order);
    Order updateOrder(Integer id, Order orderData);
    void deleteOrder(Integer id);
    int generateEmployeeId();

    // Thống kê
    long countTotalOrders();
    long countCompletedOrders();
    long countProcessingOrders();
    long countCancelledOrders();
    double calculateTotalRevenue();
    List<Map<String, Object>> getTopSellingProducts(int limit);
}
