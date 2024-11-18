package com.application.salesmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Blob;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @JsonIgnore
    @Column(name = "image_Data", nullable = false)
    private Blob imageContent;

    @ManyToOne(fetch = FetchType.LAZY)  // This indicates many images can belong to one product
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;  // This is the reference to the associated product

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Blob getImageContent() {
        return imageContent;
    }

    public void setImageContent(Blob imageContent) {
        this.imageContent = imageContent;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
