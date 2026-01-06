package com.example.roleslz_backend.Tables.comentarios.DTO;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ComentariosDTOTimeStamp(
        @NotNull UserEntity user,
        @NotBlank String title,
        @NotBlank String comentario,
        @NotNull EventoEntity evento,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) {
}
