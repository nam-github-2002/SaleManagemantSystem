package com.application.salesmanagementsystem.model;

import jakarta.persistence.*;

import java.sql.Blob;

@Entity
@Table(name="Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Lob
    @Column(name = "image_Data")
    private Blob imageContent;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Blob getImageContent() {
        return imageContent;
    }

    public void setImageContent(Blob imageContent) {
        this.imageContent = imageContent;
    }
}
