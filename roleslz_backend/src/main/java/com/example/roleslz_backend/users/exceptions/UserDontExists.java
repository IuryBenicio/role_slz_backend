package com.example.roleslz_backend.users.exceptions;

public class UserDontExists extends RuntimeException {
  public UserDontExists(String message) {
    super(message);
  }
}
