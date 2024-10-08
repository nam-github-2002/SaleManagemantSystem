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
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public void saveCustomer(Customer customer) {
        // Tạo ID mới nếu chưa có
        if (customer.getCustomerID() == null) {
            customer.setCustomerID(generateCustomerID());
        }

        // In ra tất cả các trường của khách hàng trước khi lưu
        System.out.println("Customer Data:");
        System.out.println("Mã Khách Hàng: " + customer.getCustomerID());
        System.out.println("Tên Công Ty: " + customer.getCompanyName());
        System.out.println("Điện Thoại: " + customer.getPhone());
        System.out.println("Địa Chỉ: " + customer.getAddress());
        System.out.println("Email: " + customer.getEmail());

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
        String newId = "CUST000000"; // Mặc định

        // Tìm CustomerID lớn nhất
        List<Customer> customers = customerRepository.findAll();
        if (!customers.isEmpty()) {
            String maxId = customers.stream()
                    .map(Customer::getCustomerID)
                    .max(String::compareTo)
                    .orElse(newId);
            // Tạo ID mới
            newId = "CUST" + String.format("%06d", Integer.parseInt(maxId.substring(4)) + 1);

        }
        return newId;
    }
}