package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsMapperClass {

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
        entity.setEventos(dto.eventos());
        entity.setHistoricoEventos(dto.historicoEventos());
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
                entity.getEventos(),
                entity.getHistoricoEventos(),
                entity.getFcmToken()
        );
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
            entity.setEventos(dto.eventos());
        }
        if (dto.historicoEventos() != null) {
            entity.setHistoricoEventos(dto.historicoEventos());
        }
        if (dto.fcmToken() != null) {
            entity.setFcmToken(dto.fcmToken());
        }
    }
}
