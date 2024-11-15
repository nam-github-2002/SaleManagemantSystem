package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService ordersService;

    // Hiển thị danh sách đơn hàng
    @GetMapping
    public String listOrder(Model model, HttpSession session, HttpServletRequest request) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "login";
        }
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order :: orderPage";
        }

        List<Order> orders = ordersService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/order";
    }

    // Hiển thị chi tiết hoá đơn
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable int id, Model model,
                                 HttpSession session, HttpServletRequest request) {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Order> order = ordersService.getOrderById(id);
        if (order.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newOrder", order.get());
        } else {
            model.addAttribute("error", "Không tìm thấy hoá đơn.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "order/order-form";
    }

    // Hiển thị chi tiết hoá đơn
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model,
                               HttpSession session, HttpServletRequest request) {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Order> order = ordersService.getOrderById(id);
        if (order.isPresent()) {
            model.addAttribute("viewMode", false);
            model.addAttribute("editMode", true);
            model.addAttribute("exist", true);
            model.addAttribute("newOrder", order.get());
        } else {
            model.addAttribute("error", "Không tìm thấy hoá đơn.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "order/order-form";
    }


    // Hiển thị chi tiết hoá đơn
    @GetMapping("/new")
    public String showCreateForm(Model model,
                               HttpSession session, HttpServletRequest request) {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Order newOrder = new Order();
        newOrder.setOrderId(ordersService.generateEmployeeId());

        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", true);
        model.addAttribute("newOrder", newOrder);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "order/order-form";
    }


    // Thống kê đơn hàng
    @GetMapping("/statistics")
    public String orderStatistics(Model model, HttpSession session) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "login";
        }

        long totalOrders = ordersService.countTotalOrders();
        long completedOrders = ordersService.countCompletedOrders();
        long processingOrders = ordersService.countProcessingOrders();
        long cancelledOrders = ordersService.countCancelledOrders();
        double totalRevenue = ordersService.calculateTotalRevenue();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("currentUser", loggedInUser);

        return "order/statistics";
    }

    // API lấy danh sách sản phẩm bán chạy nhất
    @GetMapping("/top-selling-products")
    public String topSellingProducts(@RequestParam(defaultValue = "5") int limit,
                                     Model model, HttpSession session,
                                     HttpServletRequest request) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "login";
        }
        List<Map<String, Object>> topProducts = ordersService.getTopSellingProducts(limit);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/top-products :: topProducts";
        }

        return "order/top-products";
    }
}
