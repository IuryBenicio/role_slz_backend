package com.example.roleslz_backend.media.storage.dto;

public record UploadSignatureDTO(
        String signature,
        String apiKey,
        String cloudName,
        String folder,
        long timestamp
) {}
