package com.example.roleslz_backend.users.mapper;

import com.example.roleslz_backend.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.users.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailsMapper {
    UserEntity toEntity(UserDTODetails dto);

    UserDTODetails toDTO(UserEntity entity);
}
