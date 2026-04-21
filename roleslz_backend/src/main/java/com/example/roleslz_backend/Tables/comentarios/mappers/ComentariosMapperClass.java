package com.example.roleslz_backend.Tables.comentarios.mappers;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import org.springframework.stereotype.Component;

@Component
public class ComentariosMapperClass {

    public ComentarioEntity toEntity(ComentariosDTO dto) {
        if (dto == null) {
            return null;
        }

        ComentarioEntity entity = new ComentarioEntity();
        entity.setUser(dto.user());
        entity.setTitle(dto.title());
        entity.setComentario(dto.comentario());
        entity.setEvento(dto.evento());

        return entity;
    }

    public ComentariosDTO toDTO(ComentarioEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ComentariosDTO(
                entity.getUser(),
                entity.getTitle(),
                entity.getComentario(),
                entity.getEvento()
        );
    }

    public void updateEntityFromDto(ComentariosDTO dto, ComentarioEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.user() != null) {
            entity.setUser(dto.user());
        }
        if (dto.title() != null) {
            entity.setTitle(dto.title());
        }
        if (dto.comentario() != null) {
            entity.setComentario(dto.comentario());
        }
        if (dto.evento() != null) {
            entity.setEvento(dto.evento());
        }
    }
}
