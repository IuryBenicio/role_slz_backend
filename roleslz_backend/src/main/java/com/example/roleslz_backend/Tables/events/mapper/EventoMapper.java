package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoEntity toEntity(EventoDTO dto);

    EventoDTO toDTO(EventoEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventoDTO dto, @MappingTarget EventoEntity entity);

    @Mapping(target = "id", ignore = true)
    EventoEntity replicate(EventoEntity original);
}
