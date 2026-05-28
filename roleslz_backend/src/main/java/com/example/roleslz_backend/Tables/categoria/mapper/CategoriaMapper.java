package com.example.roleslz_backend.Tables.categoria.mapper;

import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTORequestClass;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTOResponse;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class CategoriaMapper {

    private EventoRepository eventoRepository;

    public CategoriaMapper(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public CategoriaEntity toEntity(CategoriaDTORequestClass dto){
        if(dto == null){
            return null;
        }

        CategoriaEntity entity = new CategoriaEntity();

        entity.setName(dto.getName());

        if(!dto.getEventos().isEmpty()) {
            List<Long> ids = dto.getEventos().stream().map(EventDTOSumary::getId).toList();

            List<EventoEntity> eventos = eventoRepository.findAllById(ids);

            entity.setEventos(new HashSet<>(eventos));
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
        if(!dto.getEventos().isEmpty()) {
            List<Long> ids = dto.getEventos().stream().map(e -> e.getId()).toList();

            List<EventoEntity> eventos = eventoRepository.findAllById(ids);

            entity.setEventos(new HashSet<>(eventos));
        }
        return dto;
    }

    public CategoriaDTORequestClass toDTORequest(CategoriaEntity entity){
        if(entity == null){
            return null;
        }

        CategoriaDTORequestClass dto = new CategoriaDTORequestClass();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if(!dto.getEventos().isEmpty()){
            List<Long> ids = dto.getEventos().stream().map(EventDTOSumary::getId).toList();

            List<EventoEntity> eventos = eventoRepository.findAllById(ids);

            entity.setEventos(new HashSet<>(eventos));
        }
        return dto;
    }

}
