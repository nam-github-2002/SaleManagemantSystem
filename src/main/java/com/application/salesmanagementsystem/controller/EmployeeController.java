package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.EmployeeService;
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
        // Kiểm tra người dùng đã đăng nhập chưa
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng tới trang login
        }

        // Thêm thông tin người dùng vào model
        model.addAttribute("currentUser", loggedInUser);

        // Lấy danh sách nhân viên và thêm vào model
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);

        // Trả về trang "employee/employee-list" (file employee-list.html)
        return "employee/employee-list"; // Trả về trang employee-list.html
    }
}
