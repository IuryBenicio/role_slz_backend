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
public class EventDTOClass {

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Point local;
    private String enderecoExtenso;
    private UserDTODetails organizador;
    private Set<UserDTODetails> confirmacoes;
    private String imageUrl;
    private EstadoEvento estadoEvento;
    private Set<ComentariosDTO> comentarios;
    private Set<AvaliacaoDTO> avaliacoes;
    private Set<CategoriaEntity> categorias;
    private BigDecimal price;
    private Double latitude;
    private Double longitude;
    private Point spot;

}
