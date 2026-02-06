package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTORequest;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoEntity toEntity(EventoDTORequest dto);

    EventoDTO toDTO(EventoEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventoDTO dto, @MappingTarget EventoEntity entity);
}
