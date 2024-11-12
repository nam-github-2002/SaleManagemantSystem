package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    void createSupplier(Supplier supplier);

    Page<Supplier> getAllSuppliers(Pageable pageable);
    List<Supplier> getAllSuppliers();

    Optional<Supplier> getSupplierById(Integer id);

    void saveSupplier(Supplier supplier);

    void deleteSupplier(Integer id);

    Page<Supplier> findByName(String name, Pageable pageable);

    Optional<Supplier> findById(int id);

    int generateSupplierID();
}
