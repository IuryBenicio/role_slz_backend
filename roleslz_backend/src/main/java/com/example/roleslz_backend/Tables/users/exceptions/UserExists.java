package com.example.roleslz_backend.Tables.users.exceptions;

public class UserExists extends RuntimeException {
    public UserExists(String message) {
        super(message);
    }
}
