package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Image;

import java.util.List;

public interface ImageService {
     Image create(Image image);
     List<Image> viewAll();
     Image viewById(int id);
}
