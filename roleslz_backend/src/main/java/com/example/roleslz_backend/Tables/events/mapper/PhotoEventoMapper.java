package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.DTO.PhotosEventoDTORes;
import com.example.roleslz_backend.Tables.events.entity.PhotosEventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import org.springframework.stereotype.Component;

@Component
public class PhotoEventoMapper {
    private EventoRepository eventoRepository;

    public PhotoEventoMapper(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public PhotosEventoDTORes toDTO(PhotosEventoEntity entity) {
        if (entity == null) {
            return null;
        }
        PhotosEventoDTORes dto = new PhotosEventoDTORes();
        dto.setId(entity.getId());
        dto.setLink(entity.getLink());
        dto.setEvento(EventDTOSumary.toSummaryDTO(entity.getEvento()));
        return dto;
    }

    public PhotosEventoEntity toEntity(PhotosEventoDTORes dto) {
        if (dto == null) {
            return null;
        }
        PhotosEventoEntity entity = new PhotosEventoEntity();
        entity.setId(dto.getId());
        entity.setLink(dto.getLink());
        entity.setEvento(eventoRepository.getReferenceById(dto.getEvento().getId()));
        return entity;
    }

}
