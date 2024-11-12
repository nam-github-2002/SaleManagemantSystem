package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Page<Supplier> findBySupplierName(String supplierName, Pageable pageable);
    Supplier findTopByOrderBySupplierIDDesc();
}
