package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Orders;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.OrdersService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.persistence.criteria.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping
    public String listOrder(Model model, HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng tới trang login
        }

        // Thêm thông tin người dùng vào model
        model.addAttribute("currentUser", loggedInUser);

        // Lấy danh sách đơn hàng và thêm vào model
        List<Orders> orders = ordersService.getAllOrders();
        model.addAttribute("orders", orders);

        // Trả về trang "orders/order-list" (file order-list.html)
        return "order/order-list"; // Trả về trang order-list.html
    }
}
