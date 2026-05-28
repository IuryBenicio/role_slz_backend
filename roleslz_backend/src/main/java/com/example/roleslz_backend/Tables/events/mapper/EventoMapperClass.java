package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.avaliacao.mapper.AvaliacaoMapperClass;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaSumaryDTO;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.categoria.repository.CategoriaRepository;
import com.example.roleslz_backend.Tables.comentarios.mappers.ComentariosMapperClass;
import com.example.roleslz_backend.Tables.events.DTO.*;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;


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
        dto.setOrganizador(UserSummaryDTO.toSummaryDTO(entity.getOrganizador()));

        dto.setConfirmacoes(users.stream()
                .map(UserSummaryDTO::toSummaryDTO)
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
        dto.setCategorias(entity.getCategorias().stream()
                .map(CategoriaSumaryDTO::toSummaryDTO).collect(Collectors.toSet()));

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
        dto.setImageUrl(projection.getImageUrl());
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
        Set<UserEntity> users = entity.getConfirmacoes()
                .stream()
                .map(e-> userRepository.getReferenceById(e.getId()))
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
        dto.setOrganizador(UserSummaryDTO.toSummaryDTO(entity.getOrganizador()));

        dto.setEventType(entity.getEventType());
        dto.setEventPrivacity(entity.getEventPrivacity());


        dto.setConfirmacoes(users.stream()
                .map(UserSummaryDTO::toSummaryDTO)
                .collect(Collectors.toSet()));

        dto.setImageUrl(entity.getImageUrl());
        dto.setEstadoEvento(entity.getEstadoEvento());
        dto.setPrice(entity.getPrice());
        dto.setLatitude(y);
        dto.setLongitude(x);

        if(dto.getComentarios() != null){
            dto.setComentarios(entity.getComentarios().stream()
                    .map(comentariosMapper::toDTO).collect(Collectors.toSet()));
        }
        if(dto.getAvaliacoes() != null){
                dto.setAvaliacoes(entity.getAvaliacoes().stream()
                .map(avaliacaoMapperClass::toDto).collect(Collectors.toSet()));
        }
        if(dto.getCategorias() != null){
            dto.setCategorias(entity.getCategorias().stream()
                    .map(CategoriaSumaryDTO::toSummaryDTO).collect(Collectors.toSet()));
        }

        return dto;
    }

    public EventoDTOWithIDClass toDTOWithID(EventoEntity entity) {
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


        dto.setConfirmacoes(users.stream()
                .map(UserSummaryDTO::toSummaryDTO)
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
        dto.setCategorias(entity.getCategorias().stream().map(CategoriaSumaryDTO::toSummaryDTO).collect(Collectors.toSet()));

        return dto;
    }

    public EventoEntity toEntity(EventDTORequestClass dto){
        EventoEntity entity = new EventoEntity();

        entity.setTitle(dto.getTitle());
        entity.setEstadoEvento(dto.getEstadoEvento());
        UserEntity organizadorEntity = userRepository.getReferenceById(dto.getOrganizador().Id());
        entity.setOrganizador(organizadorEntity);
        entity.setPrice(dto.getPrice());
        entity.setConfirmacoes(dto.getConfirmacoes().stream()
                .map(summary -> userRepository.getReferenceById(summary.Id()))
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
        if (entity.getCategorias() != null){
            Set<CategoriaEntity> categoriasEntity = dto.getCategorias().stream()
                    .map(sumary -> categoriaRepository.getReferenceById(sumary.getId()))
                    .collect(Collectors.toSet());

            entity.setCategorias(categoriasEntity);
        }
        entity.setEventType(dto.getEventType());
        entity.setEventPrivacity(dto.getEventPrivacity());

        if (dto.getLatitude() != null && dto.getLongitude() != null) {

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
            Set<CategoriaEntity> categoriasEntity = dto.getCategorias().stream()
                    .map(e-> categoriaRepository.getReferenceById(e.getId()))
                    .collect(Collectors.toSet());

            entity.setCategorias(categoriasEntity);
        }

        if(dto.getOrganizador() != null){
            UserEntity organizadorEntity = userRepository.getReferenceById(dto.getOrganizador().Id());

            entity.setOrganizador(organizadorEntity);
        }
        if(dto.getComentarios() != null){
            entity.setComentarios(dto.getComentarios().stream().map(comentariosMapper::toEntity).collect(Collectors.toSet()));
        }

        if (dto.getEventPrivacity() != null){
            entity.setEventPrivacity(dto.getEventPrivacity());
        }

        if (dto.getEventType() != null){
            entity.setEventType(dto.getEventType());
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
