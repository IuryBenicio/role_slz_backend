package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTORegister(
        @NotBlank String nome,
        @NotBlank String sobrenome,
        @NotNull Sexo sexo,
        @NotBlank String email,
        @NotBlank String password,
        @NotNull Integer idade) {
}
