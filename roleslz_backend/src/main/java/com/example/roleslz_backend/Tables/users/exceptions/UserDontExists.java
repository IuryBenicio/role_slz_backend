package com.example.roleslz_backend.Tables.users.exceptions;

public class UserDontExists extends RuntimeException {
    public UserDontExists(String message) {
        super(message);
    }
}
