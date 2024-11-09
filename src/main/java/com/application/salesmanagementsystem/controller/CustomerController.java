package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/customers")
@SuppressWarnings("unchecked")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String showCustomer(Model model, @RequestParam(defaultValue = "0") int page,
                               HttpSession session, HttpServletRequest request) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        int pageSize = 8;
        Page<Customer> customers;

        if (model.containsAttribute("customers")) {
            customers = (Page<Customer>) model.getAttribute("customers");
        } else {
            customers = customerService.getAllCustomers(PageRequest.of(page, pageSize));
        }

        model.addAttribute("customers", customers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());

        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        } else {
            model.addAttribute("error", false);
        }

        if (!model.containsAttribute("keyword")) {
            model.addAttribute("keyword", null);
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "customer/customer :: customerPage"; // Giữ nguyên nếu là Ajax
        }

        // Trả về view 'customer-list' (tức là trang customer-list.html)
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer/customer-list"; // Đây là nơi thay đổi chính, trả về trang customer-list.html
    }


    // Hiển thị chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable String id, Model model,
                                HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newCustomer", customer.get());
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "customer/customer :: customerDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer/customer-detail";
    }

    // Hiển thị form tạo mới khách hàng
    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);

        String newId = customerService.generateCustomerID();
        Customer newCustomer = new Customer();
        newCustomer.setCustomerID(newId);
        model.addAttribute("newCustomer", newCustomer);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "customer/customer-add :: customerDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer-add";
    }

    // Hiển thị form chỉnh sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }

        Optional<Customer> opCustomer = customerService.getCustomerById(id);
        if (opCustomer.isPresent()) {
            Customer customer = opCustomer.get();
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newCustomer", customer);
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "customer/customer-edit :: customerDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer-edit";
    }



    // Tạo mới khách hàng
    @PostMapping("/add")
    public String createCustomer(@ModelAttribute("newCustomer") Customer customer) {
        String newId = customerService.generateCustomerID();
        customer.setCustomerID(newId);
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    // Cập nhật thông tin khách hàng
    @PostMapping("/edit/{id}")
    public String updateCustomer(@ModelAttribute("newCustomer") Customer customer, @PathVariable String id) {
        customer.setCustomerID(id);
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    // Xóa khách hàng
    @PostMapping("/{id}")
    public String deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }

    // Tìm kiếm khách hàng
    @PostMapping("/search")
    public String searchCustomers(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        int pageSize = 8;
        Page<Customer> searchResults = customerService.searchCustomers(keyword, PageRequest.of(page, pageSize));

        if (searchResults.isEmpty()) {

            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng nào với từ khóa '" + keyword + "'.");
        } else {

            redirectAttributes.addFlashAttribute("customers", searchResults);
        }

        redirectAttributes.addFlashAttribute("keyword", keyword);
        return "redirect:/customers";
    }
}