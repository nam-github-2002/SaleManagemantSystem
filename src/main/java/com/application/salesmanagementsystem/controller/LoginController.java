package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class LoginController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login-customer")
    public String loginCustomer(@RequestParam String username, @RequestParam String password,
                                RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            Customer customer = customerService.loginCustomer(username, password);

            session.setAttribute("loggedInCustomer", customer);

            return "redirect:/";
        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/login";
        }
    }


    @PostMapping("/login-internal")
    public String loginInternal(@RequestParam String username, @RequestParam String password,
                        RedirectAttributes redirectAttributes, HttpSession session)
    {
        Employee employee = employeeService.findByUsername(username);
        if (employee != null && employeeService.checkPassword(employee, password)) {
            session.setAttribute("loggedInUser", employee);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/internal";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "redirect:/login";
        }
    }


    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công!");
        return "redirect:/login";
    }

    public static boolean isAuthenticated(HttpSession session, Model model) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");

        return loggedInUser != null || loggedInCustomer != null;
    }
}
