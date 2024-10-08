package com.application.salesmanagementsystem.controller;

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
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            redirectAttributes.addFlashAttribute("newCustomer", customer.get());
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
            redirectAttributes.addFlashAttribute("newCustomer", new Customer());
        }
        return "redirect:/customers"; // Chuyển hướng về trang danh sách khách hàng
    }

    @GetMapping("/customers")
    public String showCustomer(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);

        // Tạo mã khách hàng mới
        String newId = customerService.generateCustomerID();
        model.addAttribute("newId", newId);

        // Kiểm tra nếu không có đối tượng newCustomer trong model thì tạo mới và gán customerID
        if (!model.containsAttribute("newCustomer")) {
            Customer newCustomer = new Customer();
            newCustomer.setCustomerID(newId); // Gán mã khách hàng cho đối tượng mới
            model.addAttribute("newCustomer", newCustomer);
        }

        return "customer";
    }




    @PostMapping("/customers")
    public String saveCustomer(@ModelAttribute("newCustomer") Customer customer, RedirectAttributes redirectAttributes) {
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }


    @PostMapping("/customers/{id}")
    public String deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }


}
