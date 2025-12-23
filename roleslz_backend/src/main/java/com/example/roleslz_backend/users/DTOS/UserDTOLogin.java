package com.example.roleslz_backend.users.DTOS;

import jakarta.validation.constraints.NotBlank;

public record UserDTOLogin(
        @NotBlank String email,
        @NotBlank String password) {
}
