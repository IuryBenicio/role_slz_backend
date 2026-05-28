package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapperClass;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetailsWithId;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDetailsMapperClass {

    private final EventoMapperClass eventoMapperClass;
    private final EventoRepository eventoRepository;

    public UserDetailsMapperClass(EventoMapperClass eventoMapperClass, EventoRepository eventoRepository) {
        this.eventoMapperClass = eventoMapperClass;
        this.eventoRepository = eventoRepository;
    }

    public UserEntity toEntity(UserDTODetails dto) {
        if (dto == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setNome(dto.nome());
        entity.setSobrenome(dto.sobrenome());
        entity.setSexo(dto.sexo());
        entity.setEmail(dto.email());
        entity.setIdade(dto.idade());
        if (dto.eventos() != null) {
            List<EventoEntity> eventos = dto.eventos().stream()
                    .map(summary -> eventoRepository.getReferenceById(summary.getId())) // PERFORMANCE AQUI
                    .collect(Collectors.toList());
            entity.setEventos(eventos);
        }
        if (dto.historicoEventos() != null) {
            List<EventoEntity> historicoEventos = dto.historicoEventos().stream()
                    .map(sumary -> eventoRepository.getReferenceById(sumary.getId()))
                    .collect(Collectors.toList());
            entity.setHistoricoEventos(historicoEventos);
        }
        entity.setFcmToken(dto.fcmToken());

        return entity;
    }

    public UserDTODetails toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserDTODetails(
                entity.getNome(),
                entity.getSobrenome(),
                entity.getSexo(),
                entity.getEmail(),
                entity.getIdade(),
                entity.getEventos().stream().map(eventoMapperClass::toSummaryDTO).collect(Collectors.toList()),
                entity.getHistoricoEventos().stream().map(eventoMapperClass::toSummaryDTO).collect(Collectors.toList()),
                entity.getFollowing().stream().map(this::toSummaryDTO).collect(Collectors.toSet()),
                entity.getFollowers().stream().map(this::toSummaryDTO).collect(Collectors.toSet()),
                entity.getFcmToken()
        );
    }

    public UserDTODetailsWithId toDTOWithId(UserEntity entity){
        if (entity == null) {
            return null;
        }

        UserDTODetailsWithId dto = new UserDTODetailsWithId();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());

        dto.setEventos(entity.getEventos().stream()
                .map(eventoMapperClass::toSummaryDTO)
                .collect(Collectors.toList()));


        dto.setFollowing(entity.getFollowing().stream()
                .map(this::toSummaryDTO) // Método que retorna UserSummaryDTO
                .collect(Collectors.toSet()));

        dto.setFollowers(entity.getFollowers().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toSet()));

        return dto;

    }

    public void updateEntityFromDto(UserDTODetails dto, UserEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.nome() != null) {
            entity.setNome(dto.nome());
        }
        if (dto.sobrenome() != null) {
            entity.setSobrenome(dto.sobrenome());
        }
        if (dto.sexo() != null) {
            entity.setSexo(dto.sexo());
        }
        if (dto.email() != null) {
            entity.setEmail(dto.email());
        }
        if (dto.idade() != null) {
            entity.setIdade(dto.idade());
        }
        if (dto.eventos() != null) {
            List<EventoEntity> eventos = dto.eventos().stream()
                    .map(summary -> eventoRepository.getReferenceById(summary.getId())) // PERFORMANCE AQUI
                    .collect(Collectors.toList());
            entity.setEventos(eventos);
        }
        if (dto.historicoEventos() != null) {
            List<EventoEntity> historicoEventos = dto.historicoEventos().stream()
                    .map(sumary -> eventoRepository.getReferenceById(sumary.getId()))
                    .collect(Collectors.toList());
            entity.setHistoricoEventos(historicoEventos);
        }
        if (dto.fcmToken() != null) {
            entity.setFcmToken(dto.fcmToken());
        }
    }

    public UserSummaryDTO toSummaryDTO(UserEntity entity) {
        if (entity == null) return null;
        return new UserSummaryDTO(entity.getId(), entity.getNome(), entity.getSobrenome(), entity.getSexo(), entity.getEmail(), entity.getIdade(), entity.getFcmToken());
    }
}
