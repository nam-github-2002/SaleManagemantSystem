package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    void createSupplier(Supplier supplier);

    List<Supplier> getAllSuppliers();

    Optional<Supplier> getSupplierById(Integer id);

    Supplier saveSupplier(Supplier supplier);

    void deleteSupplier(Integer id);

    Supplier findByName(String name);

    Optional<Supplier> findById(int id);
}
