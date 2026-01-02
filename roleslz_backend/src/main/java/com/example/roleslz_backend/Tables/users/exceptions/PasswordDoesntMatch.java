package com.example.roleslz_backend.Tables.users.exceptions;

public class PasswordDoesntMatch extends RuntimeException {
    public PasswordDoesntMatch(String message) {
        super(message);
    }
}
