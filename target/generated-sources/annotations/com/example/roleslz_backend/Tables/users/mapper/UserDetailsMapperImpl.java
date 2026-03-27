package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T15:40:10-0300",
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

        userEntity.setNome( dto.nome() );
        userEntity.setSobrenome( dto.sobrenome() );
        userEntity.setSexo( dto.sexo() );
        userEntity.setEmail( dto.email() );
        userEntity.setFcmToken( dto.fcmToken() );
        userEntity.setIdade( dto.idade() );
        List<EventoEntity> list = dto.eventos();
        if ( list != null ) {
            userEntity.setEventos( new ArrayList<EventoEntity>( list ) );
        }
        List<EventoEntity> list1 = dto.historicoEventos();
        if ( list1 != null ) {
            userEntity.setHistoricoEventos( new ArrayList<EventoEntity>( list1 ) );
        }

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
        String fcmToken = null;

        nome = entity.getNome();
        sobrenome = entity.getSobrenome();
        sexo = entity.getSexo();
        email = entity.getEmail();
        idade = entity.getIdade();
        List<EventoEntity> list = entity.getEventos();
        if ( list != null ) {
            eventos = new ArrayList<EventoEntity>( list );
        }
        List<EventoEntity> list1 = entity.getHistoricoEventos();
        if ( list1 != null ) {
            historicoEventos = new ArrayList<EventoEntity>( list1 );
        }
        fcmToken = entity.getFcmToken();

        UserDTODetails userDTODetails = new UserDTODetails( nome, sobrenome, sexo, email, idade, eventos, historicoEventos, fcmToken );

        return userDTODetails;
    }

    @Override
    public void updateEntityFromDto(UserDTODetails dto, UserEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.nome() != null ) {
            entity.setNome( dto.nome() );
        }
        if ( dto.sobrenome() != null ) {
            entity.setSobrenome( dto.sobrenome() );
        }
        if ( dto.sexo() != null ) {
            entity.setSexo( dto.sexo() );
        }
        if ( dto.email() != null ) {
            entity.setEmail( dto.email() );
        }
        if ( dto.fcmToken() != null ) {
            entity.setFcmToken( dto.fcmToken() );
        }
        if ( dto.idade() != null ) {
            entity.setIdade( dto.idade() );
        }
        if ( entity.getEventos() != null ) {
            List<EventoEntity> list = dto.eventos();
            if ( list != null ) {
                entity.getEventos().clear();
                entity.getEventos().addAll( list );
            }
        }
        else {
            List<EventoEntity> list = dto.eventos();
            if ( list != null ) {
                entity.setEventos( new ArrayList<EventoEntity>( list ) );
            }
        }
        if ( entity.getHistoricoEventos() != null ) {
            List<EventoEntity> list1 = dto.historicoEventos();
            if ( list1 != null ) {
                entity.getHistoricoEventos().clear();
                entity.getHistoricoEventos().addAll( list1 );
            }
        }
        else {
            List<EventoEntity> list1 = dto.historicoEventos();
            if ( list1 != null ) {
                entity.setHistoricoEventos( new ArrayList<EventoEntity>( list1 ) );
            }
        }
    }
}
