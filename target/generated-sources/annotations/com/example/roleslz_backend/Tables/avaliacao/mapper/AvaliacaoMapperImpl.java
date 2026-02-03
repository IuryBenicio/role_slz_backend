package com.example.roleslz_backend.Tables.avaliacao.mapper;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-03T10:18:02-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class AvaliacaoMapperImpl implements AvaliacaoMapper {

    @Override
    public AvaliacaoEntity toEntity(AvaliacaoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AvaliacaoEntity avaliacaoEntity = new AvaliacaoEntity();

        return avaliacaoEntity;
    }

    @Override
    public AvaliacaoDTO toDto(AvaliacaoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        float nota = 0.0f;
        UserEntity user = null;
        EventoEntity evento = null;

        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO( nota, user, evento );

        return avaliacaoDTO;
    }
}
