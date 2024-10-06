package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.OrderDetail;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    // Lấy tất cả chi tiết đơn hàng
    List<OrderDetail> getAllOrderDetails();

    // Lấy chi tiết đơn hàng theo ID
    Optional<OrderDetail> getOrderDetailById(Integer id);

    // Lưu chi tiết đơn hàng mới hoặc cập nhật chi tiết đơn hàng hiện có
    OrderDetail saveOrderDetail(OrderDetail orderDetail);

    // Xóa chi tiết đơn hàng theo ID
    void deleteOrderDetail(Integer id);
}
