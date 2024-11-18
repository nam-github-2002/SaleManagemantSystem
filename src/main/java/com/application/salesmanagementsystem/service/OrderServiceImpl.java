package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.OrderDetail;
import com.application.salesmanagementsystem.model.OrderStatus;
import com.application.salesmanagementsystem.repository.OrderDetailRepository;
import com.application.salesmanagementsystem.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Order> findAllByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findAllOrdersByKeyword(keyword, pageable);
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

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
        // Tính tổng giá trị đơn hàng
        double totalAmount = 0;
        for (OrderDetail detail : order.getOrderDetails()) {
            totalAmount += detail.getUnitPrice() * detail.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public int updateOrderStatus(Integer id, OrderStatus orderStatus) {

        return orderRepository.updateOrderStatus(id, orderStatus);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public int generateOrderId() {
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
