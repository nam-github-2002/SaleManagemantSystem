package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh tại đây
}