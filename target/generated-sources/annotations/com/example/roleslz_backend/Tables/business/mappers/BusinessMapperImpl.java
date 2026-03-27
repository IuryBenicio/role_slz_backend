package com.example.roleslz_backend.Tables.business.mappers;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import com.example.roleslz_backend.Utills.FuncionamentoClass;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T15:40:11-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class BusinessMapperImpl implements BusinessMapper {

    @Override
    public BusinessEntity toEntity(BusinessDTO dto) {
        if ( dto == null ) {
            return null;
        }

        BusinessEntity businessEntity = new BusinessEntity();

        businessEntity.setCnpj( dto.cnpj() );
        businessEntity.setNomeFantasia( dto.nomeFantasia() );
        businessEntity.setLogoTipoUrl( dto.logoTipoUrl() );

        return businessEntity;
    }

    @Override
    public BusinessDTO toDto(BusinessEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String cnpj = null;
        String nomeFantasia = null;
        String logoTipoUrl = null;

        cnpj = entity.getCnpj();
        nomeFantasia = entity.getNomeFantasia();
        logoTipoUrl = entity.getLogoTipoUrl();

        FuncionamentoClass funcionamentoClass = null;

        BusinessDTO businessDTO = new BusinessDTO( cnpj, nomeFantasia, logoTipoUrl, funcionamentoClass );

        return businessDTO;
    }

    @Override
    public void updateEntityFromDto(BusinessDTO dto, BusinessEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.cnpj() != null ) {
            entity.setCnpj( dto.cnpj() );
        }
        if ( dto.nomeFantasia() != null ) {
            entity.setNomeFantasia( dto.nomeFantasia() );
        }
        if ( dto.logoTipoUrl() != null ) {
            entity.setLogoTipoUrl( dto.logoTipoUrl() );
        }
    }
}
