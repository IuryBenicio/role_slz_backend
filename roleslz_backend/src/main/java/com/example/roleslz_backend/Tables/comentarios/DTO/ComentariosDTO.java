package com.example.roleslz_backend.Tables.comentarios.DTO;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComentariosDTO(
        @NotNull UserEntity user,
        @NotBlank String title,
        @NotBlank String comentario,
        @NotNull EventoEntity evento
        ) {
}
