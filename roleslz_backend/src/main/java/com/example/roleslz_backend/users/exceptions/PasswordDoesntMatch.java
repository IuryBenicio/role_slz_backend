package com.example.roleslz_backend.users.exceptions;

public class PasswordDoesntMatch extends RuntimeException {
    public PasswordDoesntMatch(String message) {
        super(message);
    }
}
