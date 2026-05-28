package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.users.entity.AuthProvider;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTORegisterSocial(@NotBlank String nome,
                                    @NotBlank String sobrenome,
                                    @NotNull Sexo sexo,
                                    @NotBlank String email,
                                    @NotNull Integer idade,
                                    @NotNull AuthProvider authProvider,
                                    @NotNull String providerId) {
}
