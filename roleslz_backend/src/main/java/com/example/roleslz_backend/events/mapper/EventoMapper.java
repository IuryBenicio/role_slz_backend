package com.example.roleslz_backend.events.mapper;

import com.example.roleslz_backend.events.DTO.EventoDTO;
import com.example.roleslz_backend.events.entity.EventoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoEntity toEntity(EventoDTO dto);

    EventoDTO toDTO(EventoEntity entity);
}
