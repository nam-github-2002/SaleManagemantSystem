package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.OrderDetail;
import com.application.salesmanagementsystem.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Lấy tất cả chi tiết đơn hàng
    @Override
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    // Lấy chi tiết đơn hàng theo ID
    @Override
    public Optional<OrderDetail> getOrderDetailById(Integer id) {
        return orderDetailRepository.findById(id);
    }

    // Lưu chi tiết đơn hàng mới hoặc cập nhật chi tiết đơn hàng hiện có
    @Override
    public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    // Xóa chi tiết đơn hàng theo ID
    @Override
    public void deleteOrderDetail(Integer id) {
        orderDetailRepository.deleteById(id);
    }

    // Các phương thức khác có thể thêm ở đây
}