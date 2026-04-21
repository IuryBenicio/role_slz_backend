package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterMapperClass {
    public UserDTORegister toDTO (UserEntity entity){
        if(entity == null) return null;

        UserDTORegister dto = new UserDTORegister(
                entity.getNome(),
                entity.getSobrenome(),
                entity.getSexo(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getIdade()
        );
        return dto;
    }

    public UserEntity toEntity (UserDTORegister dto){
        if(dto == null) return null;
        UserEntity entity = new UserEntity();

        entity.setNome(dto.nome());
        entity.setSobrenome(dto.sobrenome());
        entity.setSexo(dto.sexo());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setIdade(dto.idade());

        return entity;
    }
}
