package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    Page<Order> findAllByKeyword(String keyword, Pageable pageable);
    Page<Order> getAllOrders(Pageable pageable);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Integer id);
    Order createOrder(Order order);
    int updateOrderStatus(Integer id, OrderStatus status);
    void deleteOrder(Integer id);
    int generateOrderId();

    // Thống kê
    double countTotalOrders();
    double countCompletedOrders();
    double countProcessingOrders();
    double countCancelledOrders();
    double calculateTotalRevenue();
    List<Map<String, Object>> getTopSellingProducts(int limit);
    Map<String,Long> countOrdersByStatus();
    List<Order> getRecentOrders();
}
