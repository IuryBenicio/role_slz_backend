package com.example.roleslz_backend.users.DTOS;

import jakarta.validation.constraints.NotBlank;

public record PasswordDTO(@NotBlank String password) {
}
