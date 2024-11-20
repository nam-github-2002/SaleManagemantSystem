package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.EmployeeService;
import com.application.salesmanagementsystem.service.OrderService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class HomeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/")
    public String body(HttpSession session, HttpServletRequest request, Model model) {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        // Lấy các sản phẩm mới nhất
        List<Product> latestProducts = productService.getTop10NewestProducts()
                .stream().limit(4).collect(Collectors.toList());
        double totalProducts = productService.totalProducts();

        // Thống kê đơn hàng
        double totalOrders = orderService.countTotalOrders();
        double totalRevenue = orderService.calculateTotalRevenue();
        Map<String, Long> ordersByStatus = orderService.countOrdersByStatus();

        // Số lượng khách hàng
        double totalCustomers = customerService.countTotalCustomers();

        // Sản phẩm bán chạy nhất
        Product bestSellingProduct = productService.getBestSellingProduct();
        List<Order> recentOrders = orderService.getRecentOrders();

        model.addAttribute("lastestProducts", latestProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("bestSellingProduct", bestSellingProduct);
        model.addAttribute("ordersByStatus", ordersByStatus);
        model.addAttribute("recentOrders", recentOrders);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "home/home :: dashboard";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "home/home";
    }

}