package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    // Lấy tất cả khách hàng
    List<Customer> getAllCustomers();

    // Lấy khách hàng theo ID
    Optional<Customer> getCustomerById(Integer id);

    // Lưu khách hàng mới hoặc cập nhật khách hàng hiện có
    Customer saveCustomer(Customer customer);

    // Xóa khách hàng theo ID
    void deleteCustomer(Integer id);
}
