package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTORequest;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTOResponseDistance;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventoWithDistanciaMapper {

    @Mapping(target = "lat", expression = "java(entity.getLocalizacao().getY())")
    @Mapping(target = "longitude", expression = "java(p.getLocalizacao().getX())")
    @Mapping(target = "distanciaMetros", source = "distancia_metros")
    EventoDTOResponseDistance toDTO(EventoComDistanciaProjection entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventoDTORequest dto, @MappingTarget EventoEntity entity);
}
