package com.application.salesmanagementsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer category_Id;

    private String categoryName;

    @Lob
    private byte[] image;

    public Integer getCategoryId() {
        return category_Id;
    }

    public void setCategoryId(Integer categoryId) {
        this.category_Id = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}