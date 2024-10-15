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

    // Hiển thị danh sách khách hàng, và xử lý logic tạo mới/chỉnh sửa
    @GetMapping("/customers")
    public String showCustomer(Model model) {
        // Kiểm tra xem có thuộc tính customers trong model không
        if (model.containsAttribute("customers")) {
            // Nếu có, sử dụng danh sách khách hàng tìm kiếm
            List<Customer> customers = (List<Customer>) model.getAttribute("customers");
            model.addAttribute("customers", customers);
        } else {
            // Nếu không, lấy tất cả khách hàng
            List<Customer> customers = customerService.getAllCustomers();
            model.addAttribute("customers", customers);
        }

        // Kiểm tra có lỗi hay không (khi không tìm thấy khách hàng)
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", false);
        }

        // Tạo mã khách hàng mới nếu không trong chế độ sửa
        if (!model.containsAttribute("newCustomer")) {
            String newId = customerService.generateCustomerID();
            Customer newCustomer = new Customer();
            newCustomer.setCustomerID(newId);
            model.addAttribute("newCustomer", newCustomer);
        }

        if (!model.containsAttribute("keyword")) {
            model.addAttribute("keyword", null);
        }

        if (!model.containsAttribute("modal")) {
            model.addAttribute("modal", false);
        }

        return "customer";
    }

    //Mở form thêm khách hàng
    @GetMapping("/customers/new")
    public String createCustomer(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("modal", true);
        return "redirect:/customers";
    }

    //Mở form chỉnh sửa khách hàng
    @PostMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            redirectAttributes.addFlashAttribute("modal", true);
            redirectAttributes.addFlashAttribute("newCustomer", customer.get()); // Đặt khách hàng vào newCustomer để chỉnh sửa
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
        }
        return "redirect:/customers";
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

    @PostMapping("/customers/search")
    public String searchCustomers(@RequestParam("keyword") String keyword, RedirectAttributes redirectAttributes) {
        // Gọi phương thức tìm kiếm trong service
        List<Customer> searchResults = customerService.searchCustomers(keyword);

        // Kiểm tra kết quả tìm kiếm
        if (searchResults.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng nào với từ khóa '" + keyword + "'.");
        } else {
            redirectAttributes.addFlashAttribute("customers", searchResults); // Ghi đè thuộc tính customers
        }

        // Thêm thuộc tính keyword vào model để hiển thị trong form tìm kiếm
        redirectAttributes.addAttribute("keyword", keyword);

        // Trả về trang danh sách khách hàng với kết quả tìm kiếm
        return "redirect:/customers"; // Đảm bảo tên view này khớp với template bạn đang sử dụng
    }

}
