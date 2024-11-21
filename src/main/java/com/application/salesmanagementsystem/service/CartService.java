package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Cart;
import com.application.salesmanagementsystem.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getCartItems(String userId) {
        return cartRepository.findByUserId(userId); // Lấy danh sách giỏ hàng theo user_id
    }

    public void addToCart(Cart cart) {
        cartRepository.save(cart); // Lưu hoặc cập nhật giỏ hàng
    }

    public Cart findCartByUserIdAndProductId(String userId, int productId) {
        return cartRepository.findByUserIdAndProductId(userId, productId); // Tìm sản phẩm trong giỏ hàng
    }

    public void removeCartItem(int cartId) {
        cartRepository.deleteById(cartId); // Xóa sản phẩm khỏi giỏ hàng
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId); // Xóa toàn bộ giỏ hàng của người dùng
    }
}
