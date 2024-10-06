package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Lấy tất cả khách hàng
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Lấy khách hàng theo ID
    @Override
    public Optional<Customer> getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    // Lưu khách hàng mới hoặc cập nhật khách hàng hiện có
    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // Xóa khách hàng theo ID
    @Override
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }

    // Các phương thức khác có thể thêm ở đây
}