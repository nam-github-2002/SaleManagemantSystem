package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping
public class HomeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "home";
    }

    @GetMapping("/dashboard")
    public String body(HttpSession session, HttpServletRequest request, Model model) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";
        }
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            System.out.println("Tai fragement dashboard");
            return "home :: dashboard";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        System.out.println("Tai lai toan bo trang home");
        return "home";
    }

}