package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    // Lấy khách hàng theo ID
    @Override
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public void saveCustomer(Customer customer) {
            // Lưu khách hàng vào cơ sở dữ liệu
        customerRepository.save(customer);
    }

    // Xóa khách hàng theo ID
    @Override
    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    @Override
    public String generateCustomerID() {
        // Tạo danh sách các ID hợp lệ
        Set<String> existingIds = new HashSet<>();
        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            existingIds.add(customer.getCustomerID());
        }

        // Tìm ID nhỏ nhất chưa được sử dụng
        for (int i = 1; i <= 99999; i++) {
            String newId = "CUST" + String.format("%05d", i);
            if (!existingIds.contains(newId)) {
                return newId; // Trả về ID đầu tiên chưa tồn tại
            }
        }

        throw new RuntimeException("Không còn ID nào khả dụng.");
    }

    @Override
    public Page<Customer> searchCustomers(String keyword, Pageable pageable) {
        return customerRepository.findByCompanyNameContainingIgnoreCase(keyword, pageable);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

}