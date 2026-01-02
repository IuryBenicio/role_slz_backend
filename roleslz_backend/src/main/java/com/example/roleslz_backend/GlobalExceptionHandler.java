package com.example.roleslz_backend;

import com.example.roleslz_backend.Tables.events.exceptions.EventExists;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.users.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserDontExists.class)
    public ResponseEntity<String> UserDontExists(UserDontExists ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotEdited.class)
    public ResponseEntity<String> UserNotEdited(UserNotEdited ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotCreated.class)
    public ResponseEntity<String> UserNotCreated(UserNotCreated ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(UserNotDeleted.class)
    public ResponseEntity<String> UserNotDeleted(UserNotDeleted ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(PasswordDoesntMatch.class)
    public ResponseEntity<String> PasswordDoesntMatch(PasswordDoesntMatch ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserExists.class)
    public ResponseEntity<String> UserExists(UserExists ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EventNotFounded.class)
    public ResponseEntity<String> EventNotFounded(EventNotFounded ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(EventExists.class)
    public ResponseEntity<String> EventExists(EventExists ex){
        return ResponseEntity.status(HttpStatus.FOUND).body(ex.getMessage());
    }
}
