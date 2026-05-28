package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaSumaryDTO;
import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventPrivacity;
import com.example.roleslz_backend.Tables.events.entity.EventType;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record EventoDTOWithID(
         long id,
         String title,
         String description,
         LocalDateTime startDate,
         LocalDateTime endDate,
         Point local,
         String enderecoExtenso,
         UserSummaryDTO organizador,
         Set<UserSummaryDTO>confirmacoes,
         String imageUrl,
         EstadoEvento estadoEvento,
         Set<ComentariosDTO> comentarios,
         Set<AvaliacaoDTO> avaliacoes,
         Set<CategoriaSumaryDTO> categorias,
         EventType eventType,
         EventPrivacity eventPrivacity,
         BigDecimal price,
         Double latitude,
         Double longitude,
         Point spot) {
}
