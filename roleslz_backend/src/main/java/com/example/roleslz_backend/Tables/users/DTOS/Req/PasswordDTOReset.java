package com.example.roleslz_backend.Tables.users.DTOS.Req;

import jakarta.validation.constraints.NotBlank;

public record PasswordDTOReset(
        @NotBlank String email,
        @NotBlank String codigo,
        @NotBlank String novaSenha
) {
}
