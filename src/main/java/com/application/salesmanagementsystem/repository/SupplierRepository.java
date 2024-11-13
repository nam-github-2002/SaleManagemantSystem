package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    @Query("SELECT s FROM Supplier s WHERE " +
            "STR(s.supplierID) = :keyword OR " +
            "LOWER(s.supplierName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Supplier> findAllByKeyword(String keyword, Pageable pageable);

    Supplier findTopByOrderBySupplierIDDesc();
}
