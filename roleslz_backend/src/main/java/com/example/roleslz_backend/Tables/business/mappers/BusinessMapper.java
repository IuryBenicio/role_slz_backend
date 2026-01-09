package com.example.roleslz_backend.Tables.business.mappers;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BusinessMapper {
    BusinessEntity toEntity(BusinessDTO dto);

    BusinessDTO toDto(BusinessEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BusinessDTO dto, @MappingTarget BusinessEntity entity);
}
