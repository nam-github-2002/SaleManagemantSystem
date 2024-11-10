package com.application.salesmanagementsystem.model;

import jakarta.persistence.*;

import java.sql.Blob;

@Entity
@Table(name="image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(name = "image_Data", nullable = false)
    private Blob imageContent;


    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Blob getImageContent() {
        return imageContent;
    }

    public void setImageContent(Blob imageContent) {
        this.imageContent = imageContent;
    }
}
