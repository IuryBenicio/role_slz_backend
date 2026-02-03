package com.example.roleslz_backend.Tables.business.mappers;

import com.example.roleslz_backend.Tables.business.DTO.BusinessDTO;
import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import com.example.roleslz_backend.Utills.FuncionamentoClass;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-03T10:18:02-0300",
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
        FuncionamentoClass funcionamentoClass = null;

        BusinessDTO businessDTO = new BusinessDTO( cnpj, nomeFantasia, logoTipoUrl, funcionamentoClass );

        return businessDTO;
    }

    @Override
    public void updateEntityFromDto(BusinessDTO dto, BusinessEntity entity) {
        if ( dto == null ) {
            return;
        }
    }
}
