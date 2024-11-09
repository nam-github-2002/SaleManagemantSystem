package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.service.ProductService;
import com.application.salesmanagementsystem.service.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public String listSupplier(Model model, HttpSession session) {
        // Kiểm tra người dùng đã đăng nhập chưa
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng tới trang login
        }

        // Thêm thông tin người dùng vào model
        model.addAttribute("currentUser", loggedInUser);

        // Lấy danh sách nhà cung cấp và thêm vào model
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);

        // Trả về trang "supplier/supplier-list" (file supplier-list.html)
        return "supplier/supplier-list"; // Trả về trang supplier-list.html
    }
}
