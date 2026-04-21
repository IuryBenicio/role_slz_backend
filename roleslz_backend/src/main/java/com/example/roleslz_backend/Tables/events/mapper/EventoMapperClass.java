package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.avaliacao.mapper.AvaliacaoMapperClass;
import com.example.roleslz_backend.Tables.comentarios.mappers.ComentariosMapperClass;
import com.example.roleslz_backend.Tables.events.DTO.EventDTOClass;
import com.example.roleslz_backend.Tables.events.DTO.EventDTORequestClass;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTOResponseDistanceClass;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;


import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventoMapperClass {

    private UserDetailsMapperClass userDetailsMapper;
    private AvaliacaoMapperClass avaliacaoMapperClass;
    private UserRepository userRepository;
    private ComentariosMapperClass comentariosMapper;

    public EventoMapperClass(UserDetailsMapperClass userDetailsMapper, AvaliacaoMapperClass avaliacaoMapperClass, UserRepository userRepository, ComentariosMapperClass comentariosMapper) {
        this.userDetailsMapper = userDetailsMapper;
        this.avaliacaoMapperClass = avaliacaoMapperClass;
        this.userRepository = userRepository;
        this.comentariosMapper = comentariosMapper;
    }

    public EventDTORequestClass toDTORequest(EventoEntity entity){
        Set<UserEntity> users = entity.getConfirmacoes().stream()
                .map(e -> userRepository.findById(e.getId())
                        .orElseThrow(() -> new UserNotFounded("Usuário não encontrado!")))
                .collect(Collectors.toSet());

        Double y = null;
        Double x = null;

        if (entity.getSpot() != null && entity.getSpot().getLocalizacao() != null) {
            y = entity.getSpot().getLocalizacao().getY();
            x = entity.getSpot().getLocalizacao().getX();
        }

        EventDTORequestClass dto = new EventDTORequestClass();

        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setEnderecoExtenso(entity.getEnderecoExtenso());
        dto.setOrganizador(userDetailsMapper.toDTO(entity.getOrganizador()));

        dto.setConfirmacoes(users.stream()
                .map(userDetailsMapper::toDTO)
                .collect(Collectors.toSet()));

        dto.setImageUrl(entity.getImageUrl());
        dto.setEstadoEvento(entity.getEstadoEvento());
        dto.setPrice(entity.getPrice());
        dto.setLatitude(y);
        dto.setLongitude(x);

        dto.setComentarios(entity.getComentarios().stream()
                .map(comentariosMapper::toDTO).collect(Collectors.toSet()));
        dto.setAvaliacoes(entity.getAvaliacoes().stream()
                .map(avaliacaoMapperClass::toDto).collect(Collectors.toSet()));
        dto.setCategorias(entity.getCategorias());

        return dto;
    }

    public EventoDTOResponseDistanceClass toDTOResponseDistance(EventoComDistanciaProjection projection) {
        if (projection == null) return null;

        EventoDTOResponseDistanceClass dto = new EventoDTOResponseDistanceClass();

        dto.setId(projection.getId());
        dto.setTitle(projection.getTitle());
        dto.setDescription(projection.getDescription());
        dto.setStartDate(projection.getStartDate());
        dto.setEndDate(projection.getEndDate());
        dto.setPrice(projection.getPrice());
        dto.setImageUrl(projection.getImageUrl());
        dto.setEnderecoExtenso(projection.getEnderecoExtenso());

        if (projection.getLocalizacao() != null) {
            dto.setLat(projection.getLocalizacao().getY());
            dto.setLng(projection.getLocalizacao().getX());
        }

        dto.setDistanciaMetros(projection.getDistancia_metros());

        return dto;
    }

    public EventDTOClass toDTO(EventoEntity entity) {
        Set<UserEntity> users = entity.getConfirmacoes().stream()
                .map(e -> userRepository.findById(e.getId())
                        .orElseThrow(() -> new UserNotFounded("Usuário não encontrado!")))
                .collect(Collectors.toSet());

        Double y = null;
        Double x = null;

        if (entity.getSpot() != null && entity.getSpot().getLocalizacao() != null) {
            y = entity.getSpot().getLocalizacao().getY();
            x = entity.getSpot().getLocalizacao().getX();
        }

        EventDTOClass dto = new EventDTOClass();

        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setEnderecoExtenso(entity.getEnderecoExtenso());
        dto.setOrganizador(userDetailsMapper.toDTO(entity.getOrganizador()));

        dto.setConfirmacoes(users.stream()
                .map(userDetailsMapper::toDTO)
                .collect(Collectors.toSet()));

        dto.setImageUrl(entity.getImageUrl());
        dto.setEstadoEvento(entity.getEstadoEvento());
        dto.setPrice(entity.getPrice());
        dto.setLatitude(y);
        dto.setLongitude(x);

        dto.setComentarios(entity.getComentarios().stream()
                .map(comentariosMapper::toDTO).collect(Collectors.toSet()));
        dto.setAvaliacoes(entity.getAvaliacoes().stream()
                .map(avaliacaoMapperClass::toDto).collect(Collectors.toSet()));
        dto.setCategorias(entity.getCategorias());

        return dto;
    }

    public EventoEntity toEntity(EventDTORequestClass dto){
        EventoEntity entity = new EventoEntity();

        entity.setTitle(dto.getTitle());
        entity.setEstadoEvento(dto.getEstadoEvento());
        entity.setOrganizador(userDetailsMapper.toEntity(dto.getOrganizador()));
        entity.setPrice(dto.getPrice());
        entity.setConfirmacoes(dto.getConfirmacoes().stream()
                .map(userDetailsMapper::toEntity)
                .collect(Collectors.toSet()));
        entity.setComentarios(dto.getComentarios().stream()
                .map(comentariosMapper::toEntity)
                .collect(Collectors.toSet()));
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setEnderecoExtenso(dto.getEnderecoExtenso());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setAvaliacoes(dto.getAvaliacoes().stream()
                .map(avaliacaoMapperClass::toEntity)
                .collect(Collectors.toSet()));
        entity.setCategorias(dto.getCategorias());
        if (dto.getLatitude() != null || dto.getLongitude() != null) {

            GeometryFactory geometryFactory = new GeometryFactory();

            Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            point.setSRID(4326);

            SpotEntity spot =new SpotEntity();
            spot.setLocalizacao(point);

            entity.setSpot(spot);
        }

        return entity;
    }

    public void updateEntityFromDto(EventDTORequestClass dto, EventoEntity entity){
        if(dto.getTitle() != null){
            entity.setTitle(dto.getTitle());
        }
        if(dto.getDescription() != null){
            entity.setDescription(dto.getDescription());
        }
        if(dto.getStartDate() != null){
            entity.setStartDate(dto.getStartDate());
        }
        if(dto.getEndDate() != null){
            entity.setEndDate(dto.getEndDate());
        }
        if(dto.getEnderecoExtenso() != null){
            entity.setEnderecoExtenso(dto.getEnderecoExtenso());
        }
        if(dto.getImageUrl() != null){
            entity.setImageUrl(dto.getImageUrl());
        }
        if(dto.getEstadoEvento() != null){
            entity.setEstadoEvento(dto.getEstadoEvento());
        }
        if(dto.getPrice() != null){
            entity.setPrice(dto.getPrice());
        }

        if(dto.getCategorias() != null){
            entity.setCategorias(dto.getCategorias());
        }

        if(dto.getOrganizador() != null){
            entity.setOrganizador(userDetailsMapper.toEntity(dto.getOrganizador()));
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
