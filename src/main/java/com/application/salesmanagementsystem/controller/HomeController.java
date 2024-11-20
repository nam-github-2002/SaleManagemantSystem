package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Customer;
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

import java.text.DecimalFormat;
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
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        // Lấy các sản phẩm mới nhất
        List<Product> latestProducts = productService.getTop10NewestProducts()
                .stream().limit(5).collect(Collectors.toList());
        double totalProducts = productService.totalProducts();

        // Thống kê đơn hàng
        double totalOrders = orderService.countTotalOrders();
        double totalRevenue = orderService.calculateTotalRevenue();
        String formattedRevenue = decimalFormat.format(totalRevenue);
        Map<String, Long> ordersByStatus = orderService.countOrdersByStatus();

        // Số lượng khách hàng
        double totalCustomers = customerService.countTotalCustomers();

        //Khách VIP
        List<Customer> topSpendingCustomers = customerService.getTopSpendingCustomers(5);

        // Sản phẩm bán chạy nhất
        List<Object[]> bestSellingProduct = productService.getBestSellingProduct(5);
        List<Order> recentOrders = orderService.getRecentOrders();

        for (Order order : recentOrders) {
            String formattedTotalAmount = decimalFormat.format(order.getTotalAmount()); // Định dạng tổng tiền
            order.setFormattedTotalAmount(formattedTotalAmount); // Giả sử bạn có một phương thức setter cho trường này
        }

        model.addAttribute("lastestProducts", latestProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("bestSellingProduct", bestSellingProduct);
        model.addAttribute("ordersByStatus", ordersByStatus);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("topSpendingCustomers", topSpendingCustomers);


        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "home/home :: dashboard";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "home/home";
    }

}