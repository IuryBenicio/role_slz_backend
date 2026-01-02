package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterMapper {

    UserEntity toEntity(UserDTORegister dto);

    UserDTORegister toDTO(UserEntity entity);

}
