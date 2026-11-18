package com.example.roleslz_backend.media.storage.service;

import com.example.roleslz_backend.media.storage.dto.UploadSignatureDTO;

public interface MediaStorageService {
    UploadSignatureDTO generateUploadSignature(String folder);
}
