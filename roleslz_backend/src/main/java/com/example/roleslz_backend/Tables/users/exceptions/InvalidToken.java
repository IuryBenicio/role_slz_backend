package com.example.roleslz_backend.Tables.users.exceptions;

public class InvalidToken extends RuntimeException {
    public InvalidToken(String message) {
        super(message);
    }
}
