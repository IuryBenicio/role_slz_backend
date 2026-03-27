package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTORequest;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T16:30:47-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class EventoMapperImpl implements EventoMapper {

    @Override
    public EventoEntity toEntity(EventoDTORequest dto) {
        if ( dto == null ) {
            return null;
        }

        EventoEntity eventoEntity = new EventoEntity();

        return eventoEntity;
    }

    @Override
    public EventoDTO toDTO(EventoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String title = null;
        String description = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        Point local = null;
        String enderecoExtenso = null;
        UserEntity organizador = null;
        Set<UserDTODetails> confirmacoes = null;
        String imageUrl = null;
        EstadoEvento estadoEvento = null;
        Set<ComentarioEntity> comentarios = null;
        Set<AvaliacaoEntity> avaliacoes = null;
        Set<CategoriaEntity> categorias = null;
        BigDecimal price = null;
        Point spot = null;

        EventoDTO eventoDTO = new EventoDTO( title, description, startDate, endDate, local, enderecoExtenso, organizador, confirmacoes, imageUrl, estadoEvento, comentarios, avaliacoes, categorias, price, spot );

        return eventoDTO;
    }

    @Override
    public void updateEntityFromDto(EventoDTO dto, EventoEntity entity) {
        if ( dto == null ) {
            return;
        }
    }
}
