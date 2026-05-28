package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaSumaryDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventPrivacity;
import com.example.roleslz_backend.Tables.events.entity.EventType;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record EventoDTO(@NotBlank String title,
                        @NotBlank String description,
                        @NotNull LocalDateTime startDate,
                        @NotNull LocalDateTime endDate,
                        @NotNull Point local,
                        @NotBlank String enderecoExtenso,
                        @NotNull UserSummaryDTO organizador,
                        @NotNull Set<UserSummaryDTO> confirmacoes,
                        @NotBlank String imageUrl,
                        @NotNull EstadoEvento estadoEvento,
                        @NotNull Set<ComentarioEntity> comentarios,
                        @NotNull Set<AvaliacaoEntity> avaliacoes,
                        @NotNull Set<CategoriaSumaryDTO> categorias,
                        EventType eventType,
                        EventPrivacity eventPrivacity,
                        @NotNull BigDecimal price,
                        Double latitude,
                        Double longitude,
                        @NotNull Point spot
                        ) {
}
