package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Supplier;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.SupplierService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String showSupplier(Model model, @RequestParam(defaultValue = "0") int page,
                               HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        int pageSize = 8;
        Page<Supplier> Suppliers;

        if (model.containsAttribute("Suppliers")) {

            Suppliers = (Page<Supplier>) model.getAttribute("Suppliers");;
        } else {

            Suppliers = supplierService.getAllSuppliers(PageRequest.of(page, pageSize));
        }

        model.addAttribute("suppliers", Suppliers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Suppliers.getTotalPages());

        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        } else {
            model.addAttribute("error", false);
        }

        if (!model.containsAttribute("keyword")) {
            model.addAttribute("keyword", null);
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "supplier/supplier :: supplierPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);

        return "supplier/supplier";
    }


    // Hiển thị chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable int id, Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Supplier> Supplier = supplierService.getSupplierById(id);
        if (Supplier.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newSupplier", Supplier.get());
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "supplier/supplier-form :: supplierDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "supplier/supplier-form";
    }

    // Hiển thị form tạo mới khách hàng
    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);

        int newId = supplierService.generateSupplierID();
        Supplier newSupplier = new Supplier();
        newSupplier.setSupplierID(newId);
        model.addAttribute("newSupplier", newSupplier);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "supplier/supplier-form :: supplierDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "supplier/supplier-form";
    }

    // Hiển thị form chỉnh sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model,
                               HttpSession session, HttpServletRequest request)
    {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Supplier> opSupplier = supplierService.getSupplierById(id);
        if (opSupplier.isPresent()) {
            Supplier Supplier = opSupplier.get();
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newSupplier", Supplier);
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "supplier/supplier-form :: supplierDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "supplier/supplier-form";
    }



    // Tạo mới khách hàng
    @PostMapping("/new")
    public String createSupplier(@ModelAttribute("newSupplier") Supplier Supplier) {
        int newId = supplierService.generateSupplierID();
        Supplier.setSupplierID(newId);
        supplierService.saveSupplier(Supplier);
        return "redirect:/suppliers";
    }

    // Cập nhật thông tin khách hàng
    @PostMapping("/edit/{id}")
    public String updateSupplier(@ModelAttribute("newSupplier") Supplier Supplier, @PathVariable int id) {
        Supplier.setSupplierID(id);
        supplierService.saveSupplier(Supplier);
        return "redirect:/suppliers";
    }

    // Xóa khách hàng
    @PostMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable int id) {
        supplierService.deleteSupplier(id);
        return "redirect:/suppliers";
    }

    // Tìm kiếm khách hàng
    @PostMapping("/search")
    public String searchSuppliers(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        int pageSize = 8;
        Page<Supplier> searchResults = supplierService.findByName(keyword, PageRequest.of(page, pageSize));

        if (searchResults.isEmpty()) {

            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng nào với từ khóa '" + keyword + "'.");
        } else {

            redirectAttributes.addFlashAttribute("Suppliers", searchResults);
        }

        redirectAttributes.addFlashAttribute("keyword", keyword);
        return "redirect:/suppliers";
    }
}