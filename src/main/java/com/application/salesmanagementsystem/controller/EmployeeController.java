package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.EmployeeService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String listEmployees(Model model, HttpSession session) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", loggedInUser);

        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "employee";
    }
}
