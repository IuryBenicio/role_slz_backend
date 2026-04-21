package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
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
public class EventDTORequestClass {
    String title;
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Point local;
    String enderecoExtenso;
    UserDTODetails organizador;
    Set<UserDTODetails> confirmacoes;
    String imageUrl;
    EstadoEvento estadoEvento;
    Set<ComentariosDTO> comentarios;
    Set<AvaliacaoDTO> avaliacoes;
    Set<CategoriaEntity> categorias;
    BigDecimal price;
    //SPOT
    String someSpot;
    Double latitude;
    Double longitude;

}
