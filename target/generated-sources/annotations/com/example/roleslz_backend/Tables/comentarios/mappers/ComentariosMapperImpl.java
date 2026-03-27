package com.example.roleslz_backend.Tables.comentarios.mappers;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T15:40:10-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class ComentariosMapperImpl implements ComentariosMapper {

    @Override
    public ComentarioEntity toEntity(ComentariosDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ComentarioEntity comentarioEntity = new ComentarioEntity();

        comentarioEntity.setUser( dto.user() );
        comentarioEntity.setTitle( dto.title() );
        comentarioEntity.setComentario( dto.comentario() );
        comentarioEntity.setEvento( dto.evento() );

        return comentarioEntity;
    }

    @Override
    public ComentariosDTO toDTO(ComentarioEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UserEntity user = null;
        String title = null;
        String comentario = null;
        EventoEntity evento = null;

        user = entity.getUser();
        title = entity.getTitle();
        comentario = entity.getComentario();
        evento = entity.getEvento();

        ComentariosDTO comentariosDTO = new ComentariosDTO( user, title, comentario, evento );

        return comentariosDTO;
    }

    @Override
    public void updateEntityFromDto(ComentariosDTO dto, ComentarioEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.user() != null ) {
            entity.setUser( dto.user() );
        }
        if ( dto.title() != null ) {
            entity.setTitle( dto.title() );
        }
        if ( dto.comentario() != null ) {
            entity.setComentario( dto.comentario() );
        }
        if ( dto.evento() != null ) {
            entity.setEvento( dto.evento() );
        }
    }
}
