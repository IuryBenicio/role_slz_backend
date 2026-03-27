package com.example.roleslz_backend.Tables.avaliacao.DTO;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AvaliacaoDTO(
        @NotBlank long id,
        @NotBlank float nota,
        @NotNull UserEntity user,
        @NotNull EventoEntity evento,
        @NotNull LocalDateTime createdAt
        ) {
}
