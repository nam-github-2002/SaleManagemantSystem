package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.model.Product;
import com.application.salesmanagementsystem.service.EmployeeService;
import com.application.salesmanagementsystem.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class HomeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String body(HttpSession session, HttpServletRequest request, Model model) {
        if (LoginController.isAuthenticated(session, model)) {
            return "login";
        }

        List<Product> lastestProducts = productService.getTop10NewestProducts();
        List<Product> limitedProducts = lastestProducts.stream()
                .limit(4)
                .collect(Collectors.toList());

        for(Product product : limitedProducts) {
            System.out.println("product ID: "+ product.getProductID() + ", image: " + product.getImages());
        }

        model.addAttribute("lastestProducts", limitedProducts);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "home/home :: dashboard";
        }

        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        model.addAttribute("currentUser", loggedInUser);
        return "home/home";
    }

}