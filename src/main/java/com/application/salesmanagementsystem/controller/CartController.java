package com.application.salesmanagementsystem.controller;

import com.application.salesmanagementsystem.model.Cart;
import com.application.salesmanagementsystem.model.Customer;
import com.application.salesmanagementsystem.model.Employee;
import com.application.salesmanagementsystem.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService; // Service xử lý logic giỏ hàng

    @PostMapping("/cart/add")
    @ResponseBody
    public List<Cart> addToCart(@RequestParam("productId") int productId, HttpSession session) {
        // Lấy người dùng đang đăng nhập
        Object currentUser = session.getAttribute("loggedInUser");

        if (currentUser == null) {
            throw new RuntimeException("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng."); // Chuyển hướng đến trang login nếu cần
        }

        // Xác định ID của người dùng (có thể là nhân viên hoặc khách hàng)
        String userId = null;
        if (currentUser instanceof Customer) {
            userId = ((Customer) currentUser).getCustomerID(); // Lấy ID khách hàng (String)
        } else if (currentUser instanceof Employee) {
            userId = String.valueOf(((Employee) currentUser).getEmployeeId()); // Chuyển ID nhân viên (int) sang String
        }

        if (userId == null) {
            throw new RuntimeException("Không thể xác định ID người dùng.");
        }

        // Kiểm tra nếu sản phẩm đã tồn tại trong giỏ hàng
        Cart existingCart = cartService.findCartByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            existingCart.setQuantity(existingCart.getQuantity() + 1);
            cartService.addToCart(existingCart);
        } else {
            // Nếu sản phẩm chưa tồn tại, thêm mới
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(1); // Mặc định số lượng là 1
            cartService.addToCart(cart);
        }

        // Trả về danh sách giỏ hàng hiện tại
        return cartService.getCartItems(userId);
    }
}
