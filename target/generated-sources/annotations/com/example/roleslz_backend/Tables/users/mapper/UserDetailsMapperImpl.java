package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-06T20:10:48-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class UserDetailsMapperImpl implements UserDetailsMapper {

    @Override
    public UserEntity toEntity(UserDTODetails dto) {
        if ( dto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        return userEntity;
    }

    @Override
    public UserDTODetails toDTO(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String nome = null;
        String sobrenome = null;
        Sexo sexo = null;
        String email = null;
        Integer idade = null;
        List<EventoEntity> eventos = null;
        List<EventoEntity> historicoEventos = null;

        UserDTODetails userDTODetails = new UserDTODetails( nome, sobrenome, sexo, email, idade, eventos, historicoEventos );

        return userDTODetails;
    }

    @Override
    public void updateEntityFromDto(UserDTODetails dto, UserEntity entity) {
        if ( dto == null ) {
            return;
        }
    }
}
