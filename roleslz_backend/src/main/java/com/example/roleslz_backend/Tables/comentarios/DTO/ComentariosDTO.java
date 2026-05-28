package com.example.roleslz_backend.Tables.comentarios.DTO;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComentariosDTO(
        @NotNull UserSummaryDTO user,
        @NotBlank String title,
        @NotBlank String comentario,
        @NotNull EventDTOSumary evento
        ) {
}
