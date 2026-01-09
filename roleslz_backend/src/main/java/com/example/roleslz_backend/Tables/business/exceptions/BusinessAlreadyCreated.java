package com.example.roleslz_backend.Tables.business.exceptions;

public class BusinessAlreadyCreated extends RuntimeException {
    public BusinessAlreadyCreated(String message) {
        super(message);
    }
}
