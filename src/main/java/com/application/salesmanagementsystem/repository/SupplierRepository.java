package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
}
