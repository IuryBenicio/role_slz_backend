package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
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
         UserDTODetails organizador,
         Set<UserDTODetails>confirmacoes,
         String imageUrl,
         EstadoEvento estadoEvento,
         Set<ComentariosDTO> comentarios,
         Set<AvaliacaoDTO> avaliacoes,
         Set<CategoriaEntity> categorias,
         BigDecimal price,
         Double latitude,
         Double longitude,
         Point spot) {
}
