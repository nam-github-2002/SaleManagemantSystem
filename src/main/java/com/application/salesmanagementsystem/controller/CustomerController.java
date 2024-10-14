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

    @GetMapping("/customers")
    public String showCustomer(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("error", false);

        // Tạo mã khách hàng mới
        String newId = customerService.generateCustomerID();
        model.addAttribute("newId", newId);

        // Nếu chưa có đối tượng newCustomer trong model thì tạo mới
        if (!model.containsAttribute("newCustomer")) {
            Customer newCustomer = new Customer();
            newCustomer.setCustomerID(newId);
            model.addAttribute("newCustomer", newCustomer);
        }

        return "customer";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            redirectAttributes.addFlashAttribute("newCustomer", customer.get());
            redirectAttributes.addFlashAttribute("modal", true);
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
            redirectAttributes.addFlashAttribute("newCustomer", new Customer());
        }
        return "redirect:/customers"; // Chuyển hướng về trang danh sách khách hàng
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

    // Tìm kiếm khách hàng
    @PostMapping("/customers/search")
    public String searchCustomers(@RequestParam("keyword") String keyword, RedirectAttributes redirectAttributes) {
        // Gọi phương thức tìm kiếm trong service
        List<Customer> searchResults = customerService.searchCustomers(keyword);

        // Kiểm tra kết quả tìm kiếm
        if (searchResults.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng nào với từ khóa '" + keyword + "'.");
        } else {
            redirectAttributes.addFlashAttribute("customers", searchResults);
            for (Customer customer : searchResults) {
                System.out.println(customer);
            }
        }

        // Thêm thuộc tính keyword vào model để hiển thị trong form tìm kiếm
        redirectAttributes.addFlashAttribute("keyword", keyword);

        // Tạo mã khách hàng mới
        String newId = customerService.generateCustomerID();
        redirectAttributes.addFlashAttribute("newId", newId);

        //Kiểm tra nếu không có đối tượng newCustomer trong model thì tạo mới và gán customerID
        if (!redirectAttributes.containsAttribute("newCustomer")) {
            Customer newCustomer = new Customer();
            newCustomer.setCustomerID(newId); // Gán mã khách hàng cho đối tượng mới
            redirectAttributes.addAttribute("newCustomer", newCustomer);
        }

        // Trả về trang danh sách khách hàng với kết quả tìm kiếm
        return "redirect:/customer"; // Đảm bảo tên view này khớp với template bạn đang sử dụng
    }

}
