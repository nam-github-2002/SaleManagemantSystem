package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.OrderStatus;
import com.application.salesmanagementsystem.repository.OrderDetailRepository;
import com.application.salesmanagementsystem.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Integer id, Order orderData) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setCustomer(orderData.getCustomer());
            order.setEmployee(orderData.getEmployee());
            order.setOrderDate(orderData.getOrderDate());
            order.setOrderStatus(orderData.getOrderStatus());
            order.setTotalAmount(orderData.getTotalAmount());
            return orderRepository.save(order);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public int generateEmployeeId() {
        return orderRepository.findTopByOrderByOrderIdDesc().getOrderId() + 1;
    }

    // Thống kê
    @Override
    public long countTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public long countCompletedOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.Completed);
    }

    @Override
    public long countProcessingOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.Processing);
    }

    @Override
    public long countCancelledOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.Cancelled);
    }

    @Override
    public double calculateTotalRevenue() {
        Double totalRevenue = orderRepository.calculateTotalRevenue();
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        String sql = "SELECT p.product_name AS productName, SUM(od.quantity) AS totalQuantity " +
                "FROM order_detail od " +
                "JOIN product p ON od.product_id = p.product_id " +
                "GROUP BY p.product_name " +
                "ORDER BY totalQuantity DESC " +
                "LIMIT :limit";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> results = (List<Object[]>) query.getResultList();


        return results.stream()
                .map(result -> Map.of(
                        "productName", result[0],
                        "totalQuantity", result[1]
                ))
                .toList();
    }
}
