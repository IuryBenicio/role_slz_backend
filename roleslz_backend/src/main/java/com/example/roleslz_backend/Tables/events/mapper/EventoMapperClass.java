package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.avaliacao.mapper.AvaliacaoMapperClass;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaSumaryDTO;
import com.example.roleslz_backend.Tables.categoria.repository.CategoriaRepository;
import com.example.roleslz_backend.Tables.comentarios.mappers.ComentariosMapperClass;
import com.example.roleslz_backend.Tables.events.DTO.*;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.PhotosEventoEntity;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventoMapperClass {

    private final AvaliacaoMapperClass avaliacaoMapperClass;
    private final UserRepository userRepository;
    private final ComentariosMapperClass comentariosMapper;
    private final CategoriaRepository categoriaRepository;

    public EventoMapperClass(AvaliacaoMapperClass avaliacaoMapperClass, UserRepository userRepository, ComentariosMapperClass comentariosMapper, CategoriaRepository categoriaRepository) {
        this.avaliacaoMapperClass = avaliacaoMapperClass;
        this.userRepository = userRepository;
        this.comentariosMapper = comentariosMapper;
        this.categoriaRepository = categoriaRepository;
    }

    public EventoDTOResponseDistanceClass toDTOResponseDistance(EventoComDistanciaProjection projection) {
        if (projection == null) return null;
        EventoDTOResponseDistanceClass dto = new EventoDTOResponseDistanceClass();
        dto.setId(projection.getId());
        dto.setTitle(projection.getTitle());
        dto.setDescription(projection.getDescription());
        dto.setStartDate(projection.getStartDate());
        dto.setEndDate(projection.getEndDate());
        dto.setConfirmacoes(projection.getConfirmacoesCount());
        dto.setEstadoEvento(EstadoEvento.valueOf(projection.getEstadoEvento()));
        dto.setSpotNome(projection.getSpotNome());
        dto.setSpotId(projection.getSpotId());
        dto.setOrganizadorId(projection.getOrganizadorId());
        dto.setOrganizadorNome(projection.getOrganizadorNome());
        dto.setLat(projection.getLat());
        dto.setLng(projection.getLng());
        dto.setDistanciaMetros(projection.getDistancia_metros());
        return dto;
    }

    public EventDTOClass toDTO(EventoEntity entity) {
        if (entity == null) return null;
        EventDTOClass dto = new EventDTOClass();
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setEnderecoExtenso(entity.getEnderecoExtenso());
        dto.setOrganizador(UserSummaryDTO.toSummaryDTO(entity.getOrganizador()));
        dto.setEventType(entity.getEventType());
        dto.setEventPrivacity(entity.getEventPrivacity());
        dto.setEstadoEvento(entity.getEstadoEvento());
        dto.setPrice(entity.getPrice());
        if (entity.getImages() != null) {
            dto.setImages(entity.getImages().stream()
                    .map(PhotosEventoSummaryDTO::toSummaryDTO)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public EventoDTOWithIDClass toDTOWithID(EventoEntity entity) {
        if (entity == null) return null;
        EventoDTOWithIDClass dto = new EventoDTOWithIDClass();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setEnderecoExtenso(entity.getEnderecoExtenso());
        dto.setOrganizador(UserSummaryDTO.toSummaryDTO(entity.getOrganizador()));
        dto.setEventType(entity.getEventType());
        dto.setEventPrivacity(entity.getEventPrivacity());
        dto.setEstadoEvento(entity.getEstadoEvento());
        dto.setPrice(entity.getPrice());
        if (entity.getImages() != null) {
            dto.setImages(entity.getImages().stream()
                    .map(PhotosEventoSummaryDTO::toSummaryDTO)
                    .collect(Collectors.toList()));
        }
        if (entity.getConfirmacoes() != null) {
            dto.setConfirmacoes(entity.getConfirmacoes().stream()
                    .map(UserSummaryDTO::toSummaryDTO)
                    .collect(Collectors.toSet()));
        }
        if (entity.getCategorias() != null) {
            dto.setCategorias(entity.getCategorias().stream()
                    .map(CategoriaSumaryDTO::toSummaryDTO)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public EventoEntity toEntity(EventDTORequestClass dto) {
        if (dto == null) return null;

        EventoEntity entity = new EventoEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setEnderecoExtenso(dto.getEnderecoExtenso());
        entity.setPrice(dto.getPrice());
        entity.setEstadoEvento(dto.getEstadoEvento());
        entity.setEventType(dto.getEventType());
        entity.setEventPrivacity(dto.getEventPrivacity());

        if (dto.getOrganizador() != null) {
            entity.setOrganizador(userRepository.getReferenceById(dto.getOrganizador().Id()));
        }

        if (dto.getImages() != null) {
            List<PhotosEventoEntity> photoEntities = dto.getImages().stream().map(summary -> {
                PhotosEventoEntity photoEntity = new PhotosEventoEntity();
                photoEntity.setLink(summary.getLink());
                photoEntity.setEvento(entity);
                return photoEntity;
            }).collect(Collectors.toList());
            entity.setImages(photoEntities);
        }

        if (dto.getCategorias() != null) {
            entity.setCategorias(dto.getCategorias().stream()
                    .map(cat -> categoriaRepository.getReferenceById(cat.getId()))
                    .collect(Collectors.toSet()));
        }
        
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            point.setSRID(4326);
            SpotEntity spot = new SpotEntity();
            spot.setLocalizacao(point);
            entity.setSpot(spot);
        }

        return entity;
    }

    public void updateEntityFromDto(EventDTORequestClass dto, EventoEntity entity) {
        if (dto == null || entity == null) return;

        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
        if (dto.getEnderecoExtenso() != null) entity.setEnderecoExtenso(dto.getEnderecoExtenso());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getEstadoEvento() != null) entity.setEstadoEvento(dto.getEstadoEvento());
        if (dto.getEventType() != null) entity.setEventType(dto.getEventType());
        if (dto.getEventPrivacity() != null) entity.setEventPrivacity(dto.getEventPrivacity());

        if (dto.getImages() != null) {
            if (entity.getImages() != null) {
                entity.getImages().clear();
            } else {
                entity.setImages(new ArrayList<>());
            }
            List<PhotosEventoEntity> newPhotos = dto.getImages().stream().map(summary -> {
                PhotosEventoEntity photoEntity = new PhotosEventoEntity();
                if (summary.getId() != 0) photoEntity.setId(summary.getId());
                photoEntity.setLink(summary.getLink());
                photoEntity.setEvento(entity);
                return photoEntity;
            }).collect(Collectors.toList());
            entity.getImages().addAll(newPhotos);
        }

        if (dto.getCategorias() != null) {
            entity.setCategorias(dto.getCategorias().stream()
                    .map(cat -> categoriaRepository.getReferenceById(cat.getId()))
                    .collect(Collectors.toSet()));
        }

        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            point.setSRID(4326);
            if (entity.getSpot() != null) {
                entity.getSpot().setLocalizacao(point);
            } else {
                SpotEntity newSpot = new SpotEntity();
                newSpot.setLocalizacao(point);
                entity.setSpot(newSpot);
            }
        }
    }
}
