package com.example.roleslz_backend.Tables.business.mappers;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import org.springframework.stereotype.Component;

@Component
public class BusinessMapperClass {

    public BusinessEntity toEntity(BusinessDTO dto) {
        if (dto == null) {
            return null;
        }

        BusinessEntity entity = new BusinessEntity();
        entity.setCnpj(dto.cnpj());
        entity.setNomeFantasia(dto.nomeFantasia());
        entity.setLogoTipoUrl(dto.logoTipoUrl());
        entity.setFuncionamento(dto.funcionamentoClass());

        return entity;
    }

    public BusinessDTO toDto(BusinessEntity entity) {
        if (entity == null) {
            return null;
        }

        return new BusinessDTO(
                entity.getCnpj(),
                entity.getNomeFantasia(),
                entity.getLogoTipoUrl(),
                entity.getFuncionamento()
        );
    }

    public void updateEntityFromDto(BusinessDTO dto, BusinessEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.cnpj() != null) {
            entity.setCnpj(dto.cnpj());
        }
        if (dto.nomeFantasia() != null) {
            entity.setNomeFantasia(dto.nomeFantasia());
        }
        if (dto.logoTipoUrl() != null) {
            entity.setLogoTipoUrl(dto.logoTipoUrl());
        }
        if (dto.funcionamentoClass() != null) {
            entity.setFuncionamento(dto.funcionamentoClass());
        }
    }
}
