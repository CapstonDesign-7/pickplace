package com.example.pickplace.member.service.impl;

import com.example.pickplace.member.service.ImageUploadService;
import com.example.pickplace.member.service.exception.FileDeleteException;
import com.example.pickplace.member.service.exception.FileUploadException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LocalImageUploadService implements ImageUploadService {

    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @PostConstruct
    void init() {
        try {
            Path uploadPath = Paths.get(fileDir);
            Files.createDirectories(uploadPath);
            log.info("Upload directory created: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("파일이 비어있습니다.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFileName = UUID.randomUUID().toString() + extension;

            Path targetLocation = Paths.get(fileDir).resolve(savedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File saved to: {}", targetLocation.toAbsolutePath());
            return baseUrl + savedFileName;  // 예: http://localhost:8080/images/filename.jpg
        } catch (IOException e) {
            throw new FileUploadException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadImage(file));
        }
        return urls;
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(baseUrl.length());
            Path filePath = Paths.get(fileDir).resolve(fileName);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            throw new FileDeleteException("파일 삭제에 실패했습니다.", e);
        }
    }
}
