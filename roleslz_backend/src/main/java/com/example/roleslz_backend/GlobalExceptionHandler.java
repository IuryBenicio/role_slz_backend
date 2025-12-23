package com.example.roleslz_backend;

import com.example.roleslz_backend.users.exceptions.PasswordDoesntMatch;
import com.example.roleslz_backend.users.exceptions.UserExists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public ResponseEntity<String> BusinessNotFounded(UserExists ex){
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ex.getMessage());
    }
    public ResponseEntity<String> BusinessNotFounded(PasswordDoesntMatch ex){
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ex.getMessage());
    }
}
