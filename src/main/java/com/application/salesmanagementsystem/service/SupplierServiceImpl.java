package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    // Lấy tất cả nhà cung cấp
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
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // Xóa nhà cung cấp theo ID
    @Override
    public void deleteSupplier(Integer id) {
        supplierRepository.deleteById(id);
    }

    // Các phương thức khác có thể thêm ở đây
}