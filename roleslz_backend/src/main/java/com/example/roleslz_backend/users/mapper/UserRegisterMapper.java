package com.example.roleslz_backend.users.mapper;

import com.example.roleslz_backend.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.users.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterMapper {

    UserEntity toEntity(UserDTORegister dto);

    UserDTORegister toDTO(UserEntity entity);

}
