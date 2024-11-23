package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.*;

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
        return customerRepository.findAllByKeyword(keyword, pageable);
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).get();
    }

    @Override
    public Integer countTotalCustomers() {
        return customerRepository.countTotalCustomers();
    }

    public List<Customer> getTopSpendingCustomers(int topN) {
        List<Object[]> result = customerRepository.findTopSpendingCustomers(PageRequest.of(0,5));

        List<Customer> topCustomers = new ArrayList<>();
        for (Object[] row : result) {
            Customer customer = (Customer) row[0];
            Double totalSpent = (Double) row[1];

            totalSpent = Math.floor(totalSpent * 100) / 100;

            customer.setTotalSpend(totalSpent);
            topCustomers.add(customer);
        }
        return topCustomers;
    }

    @Override
    public Customer findByAccount(String account) {
        return null;
    }

    // Phương thức đăng ký
    public Customer registerCustomer(Customer newCustomer) {
        // Kiểm tra nếu account hoặc email đã tồn tại
        if (customerRepository.findByAccount(newCustomer.getAccount()) != null) {
            throw new RuntimeException("Tên tài khoản đã tồn tại");
        }
        if (customerRepository.findByEmail(newCustomer.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Lưu customer vào cơ sở dữ liệu mà không mã hóa mật khẩu
        return customerRepository.save(newCustomer);
    }

    // Phương thức đăng nhập
    public Customer loginCustomer(String account, String password) {
        // Tìm customer theo account
        Customer customer = customerRepository.findByAccount(account);
        if (customer == null) {
            throw new RuntimeException("Account does not exist");
        }

        // Kiểm tra mật khẩu, không cần so sánh với mã hóa
        if (!password.equals(customer.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        return customer;
    }

}