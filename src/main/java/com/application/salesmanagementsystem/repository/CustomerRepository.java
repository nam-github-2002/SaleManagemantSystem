package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
//    void deleteById(String customerID);
}