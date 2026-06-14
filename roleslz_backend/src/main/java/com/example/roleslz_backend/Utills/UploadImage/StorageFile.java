package com.example.roleslz_backend.Utills.UploadImage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageFile {
    String uploadFile(MultipartFile file);
    // Adicione esta nova assinatura:
    List<String> uploadMultipleFiles(List<MultipartFile> files);
    void deleteFile(String fileUrl);
}