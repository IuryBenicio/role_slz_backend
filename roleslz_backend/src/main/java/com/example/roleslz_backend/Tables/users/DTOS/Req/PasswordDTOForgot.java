package com.example.roleslz_backend.Tables.users.DTOS.Req;

import jakarta.validation.constraints.NotBlank;

public record PasswordDTOForgot(@NotBlank String email) {
}
