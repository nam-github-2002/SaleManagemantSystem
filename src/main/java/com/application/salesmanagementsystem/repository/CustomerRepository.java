package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Page<Customer> findByCompanyNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Customer> findAll(Pageable pageable);

    Optional<Customer> findByEmail(String email);
}