package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(String userId); // Tìm giỏ hàng theo user_id
    Cart findByUserIdAndProductId(String userId, int productId); // Tìm sản phẩm trong giỏ hàng theo user_id và product_id
    void deleteByUserId(String userId); // Xóa giỏ hàng theo user_id
}
