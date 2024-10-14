package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.service.ProductService;
import com.application.salesmanagementsystem.service.SupplierService;
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
    public String listSupplier(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        return "supplier";
    }
}
