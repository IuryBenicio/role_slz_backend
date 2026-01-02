package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoEntity toEntity(EventoDTO dto);

    EventoDTO toDTO(EventoEntity entity);
}
