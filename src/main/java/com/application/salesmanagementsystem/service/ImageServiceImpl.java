package com.application.salesmanagementsystem.service;

import com.application.salesmanagementsystem.model.Image;
import com.application.salesmanagementsystem.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image create(Image image) {
        return imageRepository.save(image);
    }
    @Override
    public List<Image> viewAll() {
        return (List<Image>) imageRepository.findAll();
    }
    @Override
    public Image viewById(int id) {
        return imageRepository.findById(id).orElse(null);
    }
    @Override
    public void delete(int id) {
        imageRepository.deleteById(id);
    }

}