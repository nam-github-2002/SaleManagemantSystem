package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.repository.OrderRepository;
import com.application.salesmanagementsystem.service.CustomerService;
import com.application.salesmanagementsystem.service.EmployeeService;
import com.application.salesmanagementsystem.service.OrderService;
import com.application.salesmanagementsystem.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;


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
    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("/internal")
    public String showDashboard(HttpSession session, HttpServletRequest request, Model model) throws JsonProcessingException {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        // Lấy các sản phẩm mới nhất
        List<Product> latestProducts = productService.getTop10NewestProducts()
                .stream().limit(5).collect(Collectors.toList());
        Integer totalProducts = productService.totalProducts();

        // Thống kê đơn hàng
        Integer totalOrders = orderService.countTotalOrders();
        double totalRevenue = orderService.calculateTotalRevenue();
        String formattedRevenue = decimalFormat.format(totalRevenue);
        Map<String, Long> ordersByStatus = orderService.countOrdersByStatus();

        // Số lượng khách hàng
        Integer totalCustomers = (Integer) customerService.countTotalCustomers();

        //Khách VIP
        List<Customer> topSpendingCustomers = customerService.getTopSpendingCustomers(5);

        // Sản phẩm bán chạy nhất
        List<Object[]> bestSellingProducts = productService.getBestSellingProduct(5);

        //Hoá đơn mới nhất
        List<Order> recentOrders = orderService.getRecentOrders();

        //Doanh thu và lượng khách hàng trong 5 ngày trước
        List<Object[]> stats = orderRepository.getCustomerAndRevenueStatisticsForLast5Days();

        // Chuyển Object[] thành một danh sách các Map với tên khóa là các trường bạn cần
        List<Map<String, Object>> statistics = stats.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("orderDate", row[0]);  // row[0] là orderDate
                    stat.put("customerCount", row[1]);  // row[1] là customerCount
                    stat.put("totalRevenue", row[2]);  // row[2] là totalRevenue
                    return stat;
                })
                .collect(Collectors.toList());

        // Chuyển List thành JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStats = objectMapper.writeValueAsString(statistics);

        model.addAttribute("statisticsJson", jsonStats);


        model.addAttribute("lastestProducts", latestProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("bestSellingProduct", bestSellingProducts);
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