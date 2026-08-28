package com.example.roleslz_backend.Utills;

import java.io.Serializable;

public record StandardError(
        Integer status,
        String message,
        Long timestamp
) implements Serializable {}