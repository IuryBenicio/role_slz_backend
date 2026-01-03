package com.example.roleslz_backend.Tables.avaliacao.mapper;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvaliacaoMapper {
    AvaliacaoEntity toEntity(AvaliacaoDTO dto);

    AvaliacaoDTO toDto(AvaliacaoEntity entity);
}
