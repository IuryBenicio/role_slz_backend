package com.example.roleslz_backend.Tables.comentarios.mappers;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ComentariosMapper {
    ComentarioEntity toEntity(ComentariosDTO dto);

    ComentariosDTO toDTO(ComentarioEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ComentariosDTO dto, @MappingTarget ComentarioEntity entity);

}
