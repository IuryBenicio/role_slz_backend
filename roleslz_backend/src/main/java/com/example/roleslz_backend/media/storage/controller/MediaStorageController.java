package com.example.roleslz_backend.media.storage.controller;

import com.example.roleslz_backend.media.storage.dto.UploadSignatureDTO;
import com.example.roleslz_backend.media.storage.service.MediaStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaStorageController {

    private final MediaStorageService mediaStorageService;

    public MediaStorageController(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    @GetMapping("/upload-signature")
    @PreAuthorize("isAuthenticated()") // Requires authenticated user
    public ResponseEntity<UploadSignatureDTO> getUploadSignature(@RequestParam(required = false) String folder) {
        UploadSignatureDTO signature = mediaStorageService.generateUploadSignature(folder);
        return ResponseEntity.ok(signature);
    }
}
