package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {

    @Autowired
    private EmployeeService employeeService;

    // Trang chủ
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";  // Chuyển hướng đến trang login nếu chưa đăng nhập
        }

        // Lấy thông tin người dùng đã đăng nhập từ session
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);  // Truyền thông tin người dùng vào model

        return "home";  // Trả về trang home.html
    }


    // Trang Dashboard (có thể là một phần của trang home, được tải lại thông qua Ajax)
    @GetMapping("/dashboard")
    public String body(HttpSession session, HttpServletRequest request, Model model) {
        // Kiểm tra người dùng đã đăng nhập chưa
        if (!LoginController.isAuthenticated(session, model)) {
            return "redirect:/login";  // Nếu chưa đăng nhập, chuyển hướng đến trang login
        }

        // Kiểm tra nếu yêu cầu từ Ajax (để tải phần trang tương ứng mà không tải lại toàn bộ trang)
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            System.out.println("Tải fragment dashboard");
            return "home :: dashboard";  // Trả về phần tử dashboard của trang home
        }

        // Lấy thông tin người dùng đã đăng nhập từ session
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);  // Truyền thông tin người dùng vào model

        System.out.println("Tải lại toàn bộ trang home");
        return "home";  // Trả về trang home.html
    }

}
