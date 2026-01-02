package com.example.roleslz_backend.Tables.events.exceptions;

public class EventExists extends RuntimeException {
    public EventExists(String message) {
        super(message);
    }
}
