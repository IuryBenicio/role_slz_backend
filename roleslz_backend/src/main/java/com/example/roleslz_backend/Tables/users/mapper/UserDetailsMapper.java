package com.example.roleslz_backend.Tables.users.mapper;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailsMapper {
    UserEntity toEntity(UserDTODetails dto);

    UserDTODetails toDTO(UserEntity entity);
}
