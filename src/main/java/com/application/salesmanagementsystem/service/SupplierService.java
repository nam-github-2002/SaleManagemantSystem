package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {
    // Lấy tất cả nhà cung cấp
    List<Supplier> getAllSuppliers();

    // Lấy nhà cung cấp theo ID
    Optional<Supplier> getSupplierById(Integer id);

    // Lưu nhà cung cấp mới hoặc cập nhật nhà cung cấp hiện có
    Supplier saveSupplier(Supplier supplier);

    // Xóa nhà cung cấp theo ID
    void deleteSupplier(Integer id);
}
