package com.example.roleslz_backend;

import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoAlreadyExists;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoDoesntExists;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoNotDeleted;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoNotEdited;
import com.example.roleslz_backend.Tables.comentarios.exceptions.*;
import com.example.roleslz_backend.Tables.events.exceptions.EventExists;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.exceptions.EventoNotDeleted;
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

    @ExceptionHandler(UserNotFounded.class)
    public ResponseEntity<String> UserNotFounded(UserNotFounded ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
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

    @ExceptionHandler(AvaliacaoAlreadyExists.class)
    public ResponseEntity<String> AvaliacaoAlreadyExists(AvaliacaoAlreadyExists ex){
        return ResponseEntity.status(HttpStatus.FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AvaliacaoAlreadyExists.class)
    public ResponseEntity<String> AvaliacaoDoesntExists(AvaliacaoDoesntExists ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AvaliacaoAlreadyExists.class)
    public ResponseEntity<String> AvaliacaoNotDeleted(AvaliacaoNotDeleted ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AvaliacaoNotEdited.class)
    public ResponseEntity<String> AvaliacaoNotEdited(AvaliacaoNotEdited ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

    @ExceptionHandler(ComentarioNotFounded.class)
    public ResponseEntity<String> ComentarioNotFounded(ComentarioNotFounded ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

    @ExceptionHandler(TimeDelayComentario.class)
    public ResponseEntity<String> TimeDelayComentario(TimeDelayComentario ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ComentarioDuplicado.class)
    public ResponseEntity<String> ComentarioDuplicado(ComentarioDuplicado ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ComentarioNãoAdicionado.class)
    public ResponseEntity<String> ComentarioNãoAdicionado(ComentarioNãoAdicionado ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ComentarioNotEdited.class)
    public ResponseEntity<String> ComentarioNotEdited(ComentarioNotEdited ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

    @ExceptionHandler(ComentarioNotDeleted.class)
    public ResponseEntity<String> ComentarioNotDeleted(ComentarioNotDeleted ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

    @ExceptionHandler(EventoNotDeleted.class)
    public ResponseEntity<String> EventoNotDeleted(EventoNotDeleted ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

    @ExceptionHandler(PresenceNotConfirmated.class)
    public ResponseEntity<String> PresenceNotConfirmated(PresenceNotConfirmated ex){
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

}
