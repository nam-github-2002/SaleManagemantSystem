package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Cart;
import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Object currentUser = session.getAttribute("loggedInUser");

        if (currentUser == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }

        String customerId = null;

        // Kiểm tra loại người dùng
        if (currentUser instanceof Customer) {
            customerId = ((Customer) currentUser).getCustomerID(); // Lấy ID khách hàng
        } else if (currentUser instanceof Employee) {
            customerId = ((Employee) currentUser).getUsername(); // Lấy Username hoặc ID nhân viên
        }

        if (customerId != null) {
            List<Cart> cartItems = cartService.getCartItems(customerId);
            model.addAttribute("cartItems", cartItems);
        }

        return "shop/cart"; // Trả về trang cart.html
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam int productId, HttpSession session) {
        Object currentUser = session.getAttribute("loggedInUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        String customerId = null;

        if (currentUser instanceof Customer) {
            customerId = ((Customer) currentUser).getCustomerID();
        } else if (currentUser instanceof Employee) {
            customerId = ((Employee) currentUser).getUsername();
        }

        if (customerId != null) {
            Cart cart = new Cart();
            cart.setCustomerId(customerId);
            cart.setProductId(productId);
            cart.setQuantity(1); // Mặc định số lượng là 1
            cartService.addToCart(cart);
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam int cartId) {
        cartService.removeCartItem(cartId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        Object currentUser = session.getAttribute("loggedInUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        String customerId = null;

        if (currentUser instanceof Customer) {
            customerId = ((Customer) currentUser).getCustomerID();
        } else if (currentUser instanceof Employee) {
            customerId = ((Employee) currentUser).getUsername();
        }

        if (customerId != null) {
            cartService.clearCart(customerId);
        }

        return "redirect:/checkout";
    }
}
