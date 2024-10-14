package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByCompanyNameContainingIgnoreCase(String keyword);
}