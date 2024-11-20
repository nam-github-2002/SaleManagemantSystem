package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderOrderId(Integer orderId);
    @Query(value = "SELECT MAX(od.orderDetailId) FROM OrderDetail od")
    Integer findLastOrderDetailId();
}