package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;
public interface CustomerService {
    // Lấy tất cả khách hàng
    public Page<Customer> getAllCustomers(Pageable pageable);

    // Lấy khách hàng theo ID
    Optional<Customer> getCustomerById(String id);

    // Lưu khách hàng mới hoặc cập nhật khách hàng hiện có
    void saveCustomer(Customer customer);

    // Xóa khách hàng theo ID
    void deleteCustomer(String id);

    String generateCustomerID();

    public Page<Customer> searchCustomers(String keyword,  Pageable pageable);

    public Optional<Customer> findByEmail(String email);

    double countTotalCustomers();

    List<Customer> getTopSpendingCustomers(int topN);

}
