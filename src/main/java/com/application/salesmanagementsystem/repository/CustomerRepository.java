package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Customer;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Page<Customer> findAll(@Nullable Pageable pageable);
    Optional<Customer> findByEmail(String email);
    Customer findByNameAndPhone(String name, String phone);
    List<Customer> findByNameContainingIgnoreCase(String query);
    Customer findByCustomerID(String customerID);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.customerID) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.type) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Customer> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c")
    Integer countTotalCustomers();

    // Thống kê khách hàng theo loại (type)
    @Query("SELECT c.type, COUNT(c) FROM Customer c GROUP BY c.type")
    List<Object[]> countCustomersByType();

    @Query("SELECT o.customer, SUM(od.totalPrice) FROM Order o " +
            "JOIN o.orderDetails od " +
            "GROUP BY o.customer ORDER BY SUM(od.totalPrice) DESC")
    List<Object[]> findTopSpendingCustomers(Pageable pageable);


}