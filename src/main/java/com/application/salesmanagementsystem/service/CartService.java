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

    public List<Cart> getCartItems(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    public void addToCart(Cart cart) {
        cartRepository.save(cart);
    }

    public void removeCartItem(int cartId) {
        cartRepository.deleteById(cartId);
    }

    public void clearCart(String customerId) {
        cartRepository.deleteByCustomerId(customerId);
    }
}
