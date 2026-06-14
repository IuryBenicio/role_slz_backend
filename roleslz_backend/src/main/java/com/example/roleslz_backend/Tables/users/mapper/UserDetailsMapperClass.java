package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserEditDTO;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetailsWithId;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDetailsMapperClass {

    private final EventoRepository eventoRepository;


    public UserDetailsMapperClass(EventoRepository eventoRepository) {
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
        entity.setImage(dto.image());
        entity.setProviderId(dto.providerId());
        entity.setAuthProvider(dto.authProvider());
        if (dto.eventos() != null) {
            List<EventoEntity> eventos = dto.eventos().stream()
                    .map(summary -> eventoRepository.getReferenceById(summary.getId()))
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

    public UserEditDTO toDTOEdit(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserEditDTO(
                entity.getNome(),
                entity.getSobrenome(),
                entity.getSexo(),
                entity.getEmail(),
                entity.getIdade(),
                entity.getEventos().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                entity.getHistoricoEventos().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                entity.getProviderId(),
                entity.getAuthProvider(),
                entity.getFollowing().stream().map(UserSummaryDTO::toSummaryDTO).collect(Collectors.toSet()),
                entity.getFollowers().stream().map(UserSummaryDTO::toSummaryDTO).collect(Collectors.toSet()),
                entity.getFcmToken(),
                entity.getImage() // Adicionado
        );
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
                entity.getEventos().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                entity.getHistoricoEventos().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                entity.getProviderId(),
                entity.getAuthProvider(),
                entity.getFollowing().stream().map(UserSummaryDTO::toSummaryDTO).collect(Collectors.toSet()),
                entity.getFollowers().stream().map(UserSummaryDTO::toSummaryDTO).collect(Collectors.toSet()),
                entity.getFcmToken(),
                entity.getImage()
        );
    }

    public UserDTODetailsWithId toDTOWithId(UserEntity entity){
        if (entity == null) {
            return null;
        }

        UserDTODetailsWithId dto = new UserDTODetailsWithId();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setSobrenome(entity.getSobrenome());
        dto.setSexo(entity.getSexo());
        dto.setEmail(entity.getEmail());
        dto.setIdade(entity.getIdade());
        dto.setImage(entity.getImage());

        dto.setEventos(entity.getEventos().stream()
                .map(EventDTOSumary::toSummaryDTO).toList());

        dto.setHistoricoEventos(entity.getHistoricoEventos().stream()
                .map(EventDTOSumary::toSummaryDTO).toList());

        dto.setFollowing(entity.getFollowing().stream()
                .map(UserSummaryDTO::toSummaryDTO)
                .collect(Collectors.toSet()));

        dto.setFollowers(entity.getFollowers().stream()
                .map(UserSummaryDTO::toSummaryDTO)
                .collect(Collectors.toSet()));

        dto.setFcmToken(entity.getFcmToken());

        return dto;

    }

    public void updateEntityFromDtoEdit(UserEditDTO dto, UserEntity entity) {
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
        if (dto.image() != null) { // Adicionado
            entity.setImage(dto.image());
        }

        if(dto.providerId() != null){
            entity.setProviderId(dto.providerId());
        }

        if(dto.authProvider() != null){
            entity.setAuthProvider(dto.authProvider());
        }

        if (dto.eventos() != null) {
            List<EventoEntity> eventos = dto.eventos().stream()
                    .map(summary -> eventoRepository.getReferenceById(summary.getId())).toList();
            entity.setEventos(eventos);
        }
        if (dto.historicoEventos() != null) {
            List<EventoEntity> historicoEventos = dto.historicoEventos().stream()
                    .map(sumary -> eventoRepository.getReferenceById(sumary.getId())).toList();
            entity.setHistoricoEventos(historicoEventos);
        }
        if (dto.fcmToken() != null) {
            entity.setFcmToken(dto.fcmToken());
        }
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
        if (dto.image() != null) {
            entity.setImage(dto.image());
        }

        if(dto.providerId() != null){
            entity.setProviderId(dto.providerId());
        }

        if(dto.authProvider() != null){
            entity.setAuthProvider(dto.authProvider());
        }

        if (dto.eventos() != null) {
            List<EventoEntity> eventos = dto.eventos().stream()
                    .map(summary -> eventoRepository.getReferenceById(summary.getId())).toList();
            entity.setEventos(eventos);
        }
        if (dto.historicoEventos() != null) {
            List<EventoEntity> historicoEventos = dto.historicoEventos().stream()
                    .map(sumary -> eventoRepository.getReferenceById(sumary.getId())).toList();
            entity.setHistoricoEventos(historicoEventos);
        }
        if (dto.fcmToken() != null) {
            entity.setFcmToken(dto.fcmToken());
        }
    }

}
