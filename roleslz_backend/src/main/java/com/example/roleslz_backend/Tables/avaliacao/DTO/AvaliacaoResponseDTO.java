package com.example.roleslz_backend.Tables.avaliacao.DTO;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;

import java.time.LocalDateTime;

/**
 * Avaliação achatada para leitura (feed de avaliações de um evento).
 * Sem entidades aninhadas — evita recursão do Jackson e LazyInitializationException.
 */
public record AvaliacaoResponseDTO(
        long id,
        float nota,
        String autorNome,
        String autorImage,
        LocalDateTime createdAt
) {
    public static AvaliacaoResponseDTO fromEntity(AvaliacaoEntity entity) {
        if (entity == null) return null;
        var autor = entity.getUser();
        return new AvaliacaoResponseDTO(
                entity.getId(),
                entity.getNota(),
                autor != null ? autor.getNome() : null,
                autor != null ? autor.getImage() : null,
                entity.getCreatedAt()
        );
    }
}
