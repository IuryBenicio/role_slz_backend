package com.example.roleslz_backend.Tables.avaliacao.DTO;

import jakarta.validation.constraints.Size;

/**
 * Corpo para criar/editar a avaliação de um evento. O evento vem do path e o autor do JWT.
 * A faixa da nota (1 a 5) é validada no AvaliacaoService (bean validation não cobre float).
 */
public record AvaliacaoRequestDTO(
        float nota,

        @Size(max = 2000, message = "o comentário pode ter no máximo 2000 caracteres")
        String comentario
) {
}
