package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String home(Model model) {
        Boolean success = (Boolean) model.asMap().get("success");

        if (success != null) {
            model.addAttribute("success", success);
        } else {
            model.addAttribute("success", false);
        }

        return "home"; // Trả về tệp home.html
    }


    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login"; // Trả về tệp login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        Employee employee = employeeService.findByUsername(username);

        if (employee != null && employeeService.checkPassword(employee, password)) {
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success", false);
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công!");
        return "redirect:/"; // Điều hướng đến trang đăng nhập
    }

}