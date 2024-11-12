package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public void createSupplier(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    // Lấy tất cả nhà cung cấp
    @Override
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }
    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }


    // Lấy nhà cung cấp theo ID
    @Override
    public Optional<Supplier> getSupplierById(Integer id) {
        return supplierRepository.findById(id);
    }

    // Lưu nhà cung cấp mới hoặc cập nhật nhà cung cấp hiện có
    @Override
    public void saveSupplier(Supplier supplier) {
         supplierRepository.save(supplier);
    }

    // Xóa nhà cung cấp theo ID
    @Override
    public void deleteSupplier(Integer id) {
        supplierRepository.deleteById(id);
    }

    public Page<Supplier> findByName(String supplierName, Pageable pageable) {
        return supplierRepository.findBySupplierName(supplierName, pageable);
    }

    @Override
    public Optional<Supplier> findById(int id) {
        return supplierRepository.findById(id);
    }

    @Override
    public int generateSupplierID() {
        Supplier supplier = supplierRepository.findTopByOrderBySupplierIDDesc();
        return supplier.getSupplierID() + 1;
    }
}