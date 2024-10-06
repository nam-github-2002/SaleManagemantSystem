package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Orders;
import com.application.salesmanagementsystem.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    // Lấy tất cả đơn hàng
    @Override
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    // Lấy đơn hàng theo ID
    @Override
    public Optional<Orders> getOrderById(Integer id) {
        return ordersRepository.findById(id);
    }

    // Lưu đơn hàng mới hoặc cập nhật đơn hàng hiện có
    @Override
    public Orders saveOrder(Orders order) {
        return ordersRepository.save(order);
    }

    // Xóa đơn hàng theo ID
    @Override
    public void deleteOrder(Integer id) {
        ordersRepository.deleteById(id);
    }

    // Các phương thức khác có thể thêm ở đây
}