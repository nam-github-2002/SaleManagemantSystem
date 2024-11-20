package com.application.salesmanagementsystem.repository;


import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository  extends JpaRepository<Order, Integer> {
    Order findTopByOrderByOrderIdDesc();
    Integer countByOrderStatus(OrderStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.orderStatus = :orderStatus WHERE o.orderId = :orderId")
    int updateOrderStatus(Integer orderId, OrderStatus orderStatus);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus = 'Completed'")
    Double calculateTotalRevenue();

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.employee.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(o.orderStatus) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.paymentMethod) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(o.totalAmount AS string) LIKE CONCAT('%', :keyword, '%')")
    Page<Order> findAllOrdersByKeyword(String keyword, Pageable pageable);

    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o GROUP BY o.orderStatus")
    List<Object[]> countOrdersByStatus();

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findTop5RecentOrders(Pageable pageable);

    @Query("SELECT COUNT(*) FROM Order ")
    Integer countAllOrder();

    @Query(value = "SELECT DATE(o.order_date) as orderDate, COUNT(DISTINCT o.customer_id) as customerCount, SUM(o.total_amount) as totalRevenue " +
            "FROM orders o " +
            "WHERE o.order_date >= CURRENT_DATE - INTERVAL 5 DAY " +
            "GROUP BY DATE(o.order_date) " +
            "ORDER BY DATE(o.order_date) ASC",
            nativeQuery = true)
    List<Object[]> getCustomerAndRevenueStatisticsForLast5Days();

}