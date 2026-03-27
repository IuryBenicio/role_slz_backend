package com.example.roleslz_backend.Tables.avaliacao.mapper;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoMapperClass {

    public AvaliacaoEntity toEntity(AvaliacaoDTO dto){
        if(dto == null){
            return null;
        }

        AvaliacaoEntity entity = new AvaliacaoEntity();

        entity.setUser(dto.user());
        entity.setNota(dto.nota());
        entity.setEvento(dto.evento());
        entity.setUpdatedAt(dto.createdAt());

        return entity;
    }

    public AvaliacaoDTO toDto(AvaliacaoEntity entity){
        if(entity == null){
            return null;
        }

        AvaliacaoDTO dto = new AvaliacaoDTO(
                entity.getId(),
                entity.getNota(),
                entity.getUser(),
                entity.getEvento(),
                entity.getCreatedAt()
        );

        return dto;
    }

}
