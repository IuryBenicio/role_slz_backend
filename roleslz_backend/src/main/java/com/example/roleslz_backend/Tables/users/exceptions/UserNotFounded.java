package com.example.roleslz_backend.Tables.users.exceptions;

public class UserNotFounded extends RuntimeException {
    public UserNotFounded(String message) {
        super(message);
    }
}
