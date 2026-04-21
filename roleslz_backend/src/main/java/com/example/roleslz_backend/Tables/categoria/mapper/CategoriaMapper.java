package com.example.roleslz_backend.Tables.categoria.mapper;

import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTORequestClass;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTOResponse;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapperClass;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class CategoriaMapper {

    private EventoMapperClass eventoMapperClass;
    private EventoRepository eventoRepository;

    public CategoriaMapper(EventoMapperClass eventoMapperClass, EventoRepository eventoRepository) {
        this.eventoMapperClass = eventoMapperClass;
        this.eventoRepository = eventoRepository;
    }

    public CategoriaEntity toEntity(CategoriaDTORequestClass dto){
        if(dto == null){
            return null;
        }

        CategoriaEntity entity = new CategoriaEntity();

        entity.setName(dto.getName());

        if(dto.getEventos().size() > 0 || dto.getEventos()!= null) {
            List<Long> ids = dto.getEventos().stream().map(e -> e.id()).toList();

            List<EventoEntity> eventos = eventoRepository.findAllById(ids);

            entity.setEventos(new HashSet<EventoEntity>(eventos));
        }
        return entity;
    }

    public CategoriaDTOResponse toDTO(CategoriaEntity entity){
        if(entity == null){
            return null;
        }

        CategoriaDTOResponse dto = new CategoriaDTOResponse();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if(dto.getEventos().size() > 0 || dto.getEventos()!= null) {
            List<Long> ids = dto.getEventos().stream().map(e -> e.id()).toList();

            List<EventoEntity> eventos = eventoRepository.findAllById(ids);

            entity.setEventos(new HashSet<EventoEntity>(eventos));
        }
        return dto;
    }
}
