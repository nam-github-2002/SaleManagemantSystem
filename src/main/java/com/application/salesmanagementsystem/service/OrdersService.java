package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Orders;

import java.util.List;
import java.util.Optional;

public interface OrdersService {
    // Lấy tất cả đơn hàng
    List<Orders> getAllOrders();

    // Lấy đơn hàng theo ID
    Optional<Orders> getOrderById(Integer id);

    // Lưu đơn hàng mới hoặc cập nhật đơn hàng hiện có
    Orders saveOrder(Orders order);

    // Xóa đơn hàng theo ID
    void deleteOrder(Integer id);
}
