package com.example.roleslz_backend.Tables.business.exceptions;

public class BusinessNotDeleted extends RuntimeException {
    public BusinessNotDeleted(String message) {
        super(message);
    }
}
