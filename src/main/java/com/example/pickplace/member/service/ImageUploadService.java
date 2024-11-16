package com.example.pickplace.member.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploadService {
    String uploadImage(MultipartFile file);
    List<String> uploadImages(List<MultipartFile> files);
    void deleteImage(String imageUrl);
}