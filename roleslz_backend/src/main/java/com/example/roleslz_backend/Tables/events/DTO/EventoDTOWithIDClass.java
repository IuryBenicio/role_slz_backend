package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaSumaryDTO;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventPrivacity;
import com.example.roleslz_backend.Tables.events.entity.EventType;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDTOWithIDClass {
    long id;
    String title;
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Point local;
    String enderecoExtenso;
    UserSummaryDTO organizador;
    Set<UserSummaryDTO> confirmacoes;
    String imageUrl;
    EstadoEvento estadoEvento;
    Set<ComentariosDTO> comentarios;
    Set<AvaliacaoDTO> avaliacoes;
    Set<CategoriaSumaryDTO> categorias;
    EventType eventType;
    EventPrivacity eventPrivacity;
    BigDecimal price;
    Double latitude;
    Double longitude;
    Point spot;
}
