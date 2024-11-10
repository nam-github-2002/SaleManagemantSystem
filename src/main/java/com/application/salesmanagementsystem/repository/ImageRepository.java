package com.application.salesmanagementsystem.repository;

import com.application.salesmanagementsystem.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Integer> {
}