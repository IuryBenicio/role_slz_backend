package com.example.roleslz_backend.infra.security;

public class ErrorToGenerateToken extends RuntimeException {
    public ErrorToGenerateToken(String message) {
        super(message);
    }
}
