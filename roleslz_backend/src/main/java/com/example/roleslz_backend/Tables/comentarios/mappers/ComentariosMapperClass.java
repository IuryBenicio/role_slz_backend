package com.example.roleslz_backend.Tables.comentarios.mappers;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ComentariosMapperClass {

    private final UserRepository userRepository;
    private final EventoRepository eventoRepository;

    public ComentariosMapperClass(UserRepository userRepository, EventoRepository eventoRepository) {
        this.userRepository = userRepository;
        this.eventoRepository = eventoRepository;
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

        UserSummaryDTO usuario = UserSummaryDTO.toSummaryDTO(entity.getUser());
        EventDTOSumary evento = EventDTOSumary.toSummaryDTO(entity.getEvento());
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
