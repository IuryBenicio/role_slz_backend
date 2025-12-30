package com.example.roleslz_backend.users.DTOS;

import com.example.roleslz_backend.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;

public record UserDTODetails(
        @NotBlank String nome,
        @NotBlank String sobrenome,
        @NotBlank Sexo sexo,
        @NotBlank String email,
        @NotBlank Integer idade
) {
}
