package com.example.roleslz_backend.media.storage.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.roleslz_backend.media.storage.dto.UploadSignatureDTO;
import com.example.roleslz_backend.media.storage.service.MediaStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryStorageServiceImpl implements MediaStorageService {

    private final String apiKey;
    private final String apiSecret;
    private final String cloudName;

    public CloudinaryStorageServiceImpl(@Value("${cloudinary.api_key}") String apiKey,
                                        @Value("${cloudinary.api_secret}") String apiSecret,
                                        @Value("${cloudinary.cloud_name}") String cloudName) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.cloudName = cloudName;
    }

    @Override
    public UploadSignatureDTO generateUploadSignature(String folder) {
        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        if (folder != null && !folder.isEmpty()) {
            params.put("folder", folder);
        }

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));

        String signature = cloudinary.apiSignRequest(params, apiSecret);

        return new UploadSignatureDTO(signature, apiKey, cloudName, folder, timestamp);
    }
}
