package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.*;
import com.application.salesmanagementsystem.model.Order;
import com.application.salesmanagementsystem.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
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
    public String showListOrder(Model model, @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String keyword,
                               HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        int pageSize = 6;
        Page<Order> orders;
        boolean validKeword = keyword != null && !keyword.isEmpty() && !keyword.equalsIgnoreCase("keyword");

        if (validKeword) {

            orders = ordersService.findAllByKeyword(keyword, PageRequest.of(page, pageSize));
        } else {

            orders = ordersService.getAllOrders(PageRequest.of(page, pageSize));
        }

        model.addAttribute("orders", orders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("error", model.getAttribute("error"));
        model.addAttribute("currentUser", loggedInUser);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order :: orderPage";
        }

        return "order/order";
    }


    // Hiển thị chi tiết hoá đơn
    @GetMapping("/detail/{id}")
    public String showDetailForm(@PathVariable int id, Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Optional<Order> order = ordersService.getOrderById(id);
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (order.isPresent()) {
            model.addAttribute("viewMode", true);
            model.addAttribute("editMode", false);
            model.addAttribute("exist", true);
            model.addAttribute("newOrder", order.get());
            model.addAttribute("currentUser", employee);
        } else {
            model.addAttribute("error", "Không tìm thấy hoá đơn.");
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }


        return "order/order-form";
    }

    // Hiển thị chi tiết hoá đơn
    @GetMapping("/new")
    public String showDetailForm(Model model,
                                 HttpSession session, HttpServletRequest request)
    {
        if (!LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        Order newOrder = new Order();
        newOrder.setOrderId(ordersService.generateOrderId());
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("viewMode", false);
        model.addAttribute("editMode", true);
        model.addAttribute("exist", false);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("employee", employee);
        model.addAttribute("currentUser", employee);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "order/order-form :: orderDetailPage";
        }

        return "order/order-form";
    }


    // Thống kê đơn hàng
    @GetMapping("/statistics")
    public String orderStatistics(Model model, HttpSession session)
    {
        Order loggedInUser = (Order) session.getAttribute("loggedInUser");
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




    @PostMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer id, @RequestBody Order orderData)
    {
        // Gọi service để cập nhật trạng thái đơn hàng
        OrderStatus orderStatus = orderData.getOrderStatus();
        int updatedOrder = ordersService.updateOrderStatus(id, orderStatus);

        // Trả về phản hồi thành công
        return ResponseEntity.ok(Map.of("success", true, "updatedOrder", updatedOrder));
    }

    // Xóa đơn hàng
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable int id)
    {
        ordersService.deleteOrder(id);
        return "redirect:/orders";
    }


}
