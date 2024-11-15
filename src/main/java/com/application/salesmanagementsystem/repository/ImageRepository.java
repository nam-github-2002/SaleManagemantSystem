package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query("SELECT i.id FROM Image i WHERE i.product.productID = :productId")
    List<Integer> findAllByProductId(int productId);
}