package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Hiển thị danh sách khách hàng, và xử lý logic tạo mới/chỉnh sửa
    @GetMapping
    public String showCustomer(Model model, @RequestParam(defaultValue = "0") int page, HttpSession session) {
        int pageSize = 8;

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);

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

        return "customer/customer :: customerPage";
    }

    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable String id, Model model, HttpSession session) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);

        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);// Flag to check if it's view mode
            model.addAttribute("newCustomer", customer.get());
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }
        return "customer/customer-form";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);  // Flag to indicate create mode
        model.addAttribute("exist", false);
        String newId = customerService.generateCustomerID();
        Customer newCustomer = new Customer();
        newCustomer.setCustomerID(newId);
        model.addAttribute("newCustomer", newCustomer);

        return "customer/customer-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, HttpSession session) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);

        Optional<Customer> opCustomer = customerService.getCustomerById(id);
        if (opCustomer.isPresent()) {
            Customer customer = opCustomer.get();
            customer.setCustomerID(id);
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newCustomer", customer);
        } else {
            model.addAttribute("error", "Không tìm thấy khách hàng.");
        }
        return "customer/customer-form";
    }



    @PostMapping("/new")
    public String createCustomer(@ModelAttribute("newCustomer") Customer customer, RedirectAttributes redirectAttributes) {
        String newId = customerService.generateCustomerID();
        customer.setCustomerID(newId);
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@ModelAttribute("newCustomer") Customer customer,@PathVariable String id, RedirectAttributes redirectAttributes) {
        customer.setCustomerID(id);
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @PostMapping("/{id}")
    public String deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }

    @PostMapping("/search")
    public String searchCustomers(@RequestParam("keyword") String keyword,  @RequestParam(defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        int pageSize = 8;

        // Gọi phương thức tìm kiếm trong service
        Page<Customer> searchResults = customerService.searchCustomers(keyword, PageRequest.of(page, pageSize));

        // Kiểm tra kết quả tìm kiếm
        if (searchResults.isEmpty()) {

            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng nào với từ khóa '" + keyword + "'.");
        } else {

            redirectAttributes.addFlashAttribute("customers", searchResults);
        }

        // Thêm thuộc tính keyword vào model để hiển thị trong form tìm kiếm
        redirectAttributes.addAttribute("keyword", keyword);

        // Trả về trang danh sách khách hàng với kết quả tìm kiếm
        return "redirect:/customers"; // Đảm bảo tên view này khớp với template bạn đang sử dụng
    }

}
