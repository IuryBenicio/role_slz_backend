package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;

public record UserDTORegister(
        @NotBlank String nome,
        @NotBlank String sobrenome,
        @NotBlank Sexo sexo,
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String idade) {
}
