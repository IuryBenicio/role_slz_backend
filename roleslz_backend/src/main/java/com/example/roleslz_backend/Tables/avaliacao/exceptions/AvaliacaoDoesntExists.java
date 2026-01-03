package com.example.roleslz_backend.Tables.avaliacao.exceptions;

public class AvaliacaoDoesntExists extends RuntimeException {
    public AvaliacaoDoesntExists(String message) {
        super(message);
    }
}
