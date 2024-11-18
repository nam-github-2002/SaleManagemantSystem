package com.application.salesmanagementsystem.repository;


import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository  extends JpaRepository<Order, Integer> {
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

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.orderStatus = :orderStatus WHERE o.orderId = :orderId")
    int updateOrderStatus(Integer orderId, OrderStatus orderStatus);
    Order findTopByOrderByOrderIdDesc();
    long countByOrderStatus(OrderStatus status);


}