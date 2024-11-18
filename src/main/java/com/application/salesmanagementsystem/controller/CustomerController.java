package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.repository.CustomerRepository;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
@SuppressWarnings("unchecked")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String showCustomer(Model model, @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String keyword,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

        int pageSize = 8;
        Page<Customer> customers;
        boolean validKeword = keyword != null && !keyword.isEmpty() && !keyword.equalsIgnoreCase("keyword");

        if (validKeword) {

            customers = customerService.searchCustomers(keyword, PageRequest.of(page, pageSize));
        } else {

            customers = customerService.getAllCustomers(PageRequest.of(page, pageSize));
        }

        model.addAttribute("customers", customers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("error", model.getAttribute("error"));
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "customer/customer :: customerPage";
        }

        return "customer/customer";
    }


    // Hiển thị chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable String id, Model model,
                                HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
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
            return "customer/customer-form :: customerDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer/customer-form";
    }

    // Hiển thị form tạo mới khách hàng
    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);

        String newId = customerService.generateCustomerID();
        Customer newCustomer = new Customer();
        newCustomer.setCustomerID(newId);
        model.addAttribute("newCustomer", newCustomer);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "customer/customer-form :: customerDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer/customer-form";
    }

    // Hiển thị form chỉnh sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
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
            return "customer/customer-form :: customerDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "customer/customer-form";
    }



    // Tạo mới khách hàng
    @PostMapping("/new")
    public String createCustomer(@ModelAttribute("newCustomer") Customer customer)
    {
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
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }


    // Tìm kiếm khách hàng
    @GetMapping("/searchProductName")
    @ResponseBody
    public List<Customer> searchCustomers(@RequestParam String query) {
        return customerRepository.findByNameContainingIgnoreCase(query);
    }

}