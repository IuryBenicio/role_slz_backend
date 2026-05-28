package com.example.roleslz_backend.Tables.comentarios.mappers;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapperClass;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ComentariosMapperClass {

    private final UserRepository userRepository;
    private final EventoRepository eventoRepository;
    private final UserDetailsMapperClass userDetailsMapperClass;
    private final EventoMapperClass eventoMapperClass;

    public ComentariosMapperClass(UserRepository userRepository, EventoRepository eventoRepository, UserDetailsMapperClass userDetailsMapperClass, EventoMapperClass eventoMapperClass) {
        this.userRepository = userRepository;
        this.eventoRepository = eventoRepository;
        this.userDetailsMapperClass = userDetailsMapperClass;
        this.eventoMapperClass = eventoMapperClass;
    }

    public ComentarioEntity toEntity(ComentariosDTO dto) {
        if (dto == null) {
            return null;
        }

        ComentarioEntity entity = new ComentarioEntity();
        UserEntity user = userRepository.getReferenceById(dto.user().Id());
        entity.setUser(user);
        entity.setTitle(dto.title());
        entity.setComentario(dto.comentario());
        EventoEntity evento = eventoRepository.getReferenceById(dto.user().Id());
        entity.setEvento(evento);

        return entity;
    }

    public ComentariosDTO toDTO(ComentarioEntity entity) {
        if (entity == null) {
            return null;
        }

        UserSummaryDTO usuario = userDetailsMapperClass.toSummaryDTO(entity.getUser());
        EventDTOSumary evento = eventoMapperClass.toSummaryDTO(entity.getEvento());
        return new ComentariosDTO(
                usuario,
                entity.getTitle(),
                entity.getComentario(),
                evento
        );
    }

    public void updateEntityFromDto(ComentariosDTO dto, ComentarioEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.user() != null) {
            UserEntity user = userRepository.getReferenceById(dto.user().Id());
            entity.setUser(user);
        }
        if (dto.title() != null) {
            entity.setTitle(dto.title());
        }
        if (dto.comentario() != null) {
            entity.setComentario(dto.comentario());
        }
        if (dto.evento() != null) {
            EventoEntity evento = eventoRepository.getReferenceById(dto.user().Id());
            entity.setEvento(evento);
        }
    }
}
