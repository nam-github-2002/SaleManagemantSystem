package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Image i WHERE i.productId IS NULL")
    void deleteImagesWithNullProductId();

    @Query("SELECT i.id FROM Image i WHERE i.productId = :productId")
    List<Integer> findAllByProductId(int productId);
}