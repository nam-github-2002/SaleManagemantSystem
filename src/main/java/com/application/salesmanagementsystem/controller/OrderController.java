package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Orders;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.OrdersService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.persistence.criteria.Order;
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
    public String listOrder(Model model) {
        List<Orders> orders = ordersService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order";
    }
}
