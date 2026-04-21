package com.example.roleslz_backend.Tables.categoria.exceptions;

public class CategoriaExists extends RuntimeException {
    public CategoriaExists(String message) {
        super(message);
    }
}
