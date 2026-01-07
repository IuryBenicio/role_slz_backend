package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-06T20:10:48-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class UserRegisterMapperImpl implements UserRegisterMapper {

    @Override
    public UserEntity toEntity(UserDTORegister dto) {
        if ( dto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        return userEntity;
    }

    @Override
    public UserDTORegister toDTO(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String password = null;

        password = entity.getPassword();

        String nome = null;
        String sobrenome = null;
        Sexo sexo = null;
        String email = null;
        String idade = null;

        UserDTORegister userDTORegister = new UserDTORegister( nome, sobrenome, sexo, email, password, idade );

        return userDTORegister;
    }
}
