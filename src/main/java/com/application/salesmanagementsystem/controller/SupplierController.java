package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.service.ProductService;
import com.application.salesmanagementsystem.service.SupplierService;
import jakarta.servlet.http.HttpServletRequest;
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
    public String listSupplier(Model model, HttpSession session, HttpServletRequest request) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "supplier/supplier :: supplierPage";
        }

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        return "supplier/supplier";
    }
}
