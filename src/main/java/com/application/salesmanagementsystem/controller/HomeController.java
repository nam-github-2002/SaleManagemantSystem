package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String home(@RequestParam(required = false) Boolean success, Model model) {
        model.addAttribute("success",false);
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login"; // Trả về tệp login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Employee employee = employeeService.findByUsername(username);

        if (employee != null && employeeService.checkPassword(employee, password)) {
            model.addAttribute("success", true);
            return "index";
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "redirect:/home/login";
        }
    }

    @PostMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("success", false);
        model.addAttribute("message", "Bạn đã đăng xuất thành công!");
        return "index"; // Điều hướng đến trang đăng nhập
    }

    public void print(Employee employee) {
        System.out.println("Tên: " + employee.getName());
        System.out.println("Tên Đăng Nhập: " + employee.getUsername());
        System.out.println("Số Điện Thoại: " + employee.getPhone());
        System.out.println("Mat khau: " + employee.getPassword());
        System.out.println("Vai Trò: " + employee.getRole());
    }
}