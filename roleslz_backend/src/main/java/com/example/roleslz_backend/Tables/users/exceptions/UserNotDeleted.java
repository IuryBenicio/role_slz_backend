package com.example.roleslz_backend.Tables.users.exceptions;

public class UserNotDeleted extends RuntimeException {
    public UserNotDeleted(String message) {
        super(message);
    }
}
