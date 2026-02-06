package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTORequest;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTOResponseDistance;
import com.example.roleslz_backend.Tables.events.DTO.NewPriceDTO;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.*;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapper;
import com.example.roleslz_backend.Tables.events.mapper.EventoWithDistanciaMapper;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final EventoWithDistanciaMapper eventoWithDistanciaMapper;
    private final UserDetailsMapper userDetailsMapper;

    public EventoService(EventoRepository eventoRepository, EventoMapper eventoMapper, EventoWithDistanciaMapper eventoWithDistanciaMapper, UserDetailsMapper userDetailsMapper) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
        this.eventoWithDistanciaMapper = eventoWithDistanciaMapper;
        this.userDetailsMapper = userDetailsMapper;
    }

    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    //Services de localização
    public Set<EventoDTOResponseDistance> getEventsAround(double lat, double lng, double raioKm){
        try{
            //point se usa da Longitude e Latitude
            Point userPoint = factory.createPoint(new Coordinate(lng, lat));

            //conversão para metros
            Double raioMetros = raioKm * 1000;

            List<EventoComDistanciaProjection> eventos = eventoRepository.findEventosComDistancia(userPoint, raioMetros);

            return eventos.stream().map((a)->eventoWithDistanciaMapper.toDTO(a)).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Tivemos problemas para pegar eventos em volta " + e);
        }
    }

    public Set<EventoDTOResponseDistance> getEventosInMapArea(double minLat, double minLon, double maxLat, double maxLon){
        try{
            Point centroMapa = factory.createPoint(new Coordinate((minLon + maxLon)/2, (minLat + maxLat)/2));

            List<EventoComDistanciaProjection> eventos = eventoRepository.findEventosNoMapa(
                    minLon, minLat, maxLon, maxLat, centroMapa
            );

            return eventos.stream()
                    .map(eventoWithDistanciaMapper::toDTO)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Tivemos problemas para pegar eventos na area " + e);
        }
    }



    //Services
    public EventoDTO getEvento(long id){
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded("Evento não encontrado"));
        try{
            return eventoMapper.toDTO(evento);
        } catch (Exception e) {
            throw new EventNotFounded("Evento não retornado");
        }
    }

    public EventoDTO createEvento(EventoDTORequest eventoDTO){
        Optional<EventoEntity> exists = eventoRepository.findByTitle(eventoDTO.title());

        if(exists.isPresent()){
            throw new EventExists("Evento já existe");
        }

        if(eventoDTO.estadoEvento().equals(EstadoEvento.POS)){
            throw new EventExists("Evento com estado incorreto");
        }

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        Point localizacao = factory.createPoint(new Coordinate(eventoDTO.longitude(), eventoDTO.latitude()));

        EventoEntity novoEvento = eventoMapper.toEntity(eventoDTO);

        SpotEntity spot = new SpotEntity();

        spot.setLocalizacao(localizacao);

        novoEvento.setSpot(spot);

        EventoEntity evento = eventoRepository.save(novoEvento);

        return eventoMapper.toDTO(evento);
    }

    public void editPrice(long id, NewPriceDTO price){
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded("Evento não encontrado"));

        BigDecimal zero = BigDecimal.ZERO;

        if(price.newPrice().compareTo(zero) < 0){
            throw new InvalidPrice("Preço inválido");
        }

        try{
            evento.setPrice(price.newPrice());
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new EventNotEdited("Evento não editado");
        }

    }

    public EventoDTO editEvento(long id,EventoDTO eventoDTO){
       EventoEntity evento = eventoRepository.findById(id).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

       eventoMapper.updateEntityFromDto(eventoDTO, evento);

       EventoEntity salvo = eventoRepository.save(evento);

       return eventoMapper.toDTO(salvo);
    }

    public void deleteEvento(long id){
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        try{
            eventoRepository.delete(evento);
        } catch (Exception e) {
            throw new EventoNotDeleted("Evento não deletado");
        }
    }

    public Set<UserDTODetails> getConfirms (long eventoId){
        EventoEntity evento = eventoRepository.findById(eventoId).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        try{
            Set<UserEntity> confirms = evento.getConfirmacoes();
            Set<UserDTODetails> confirmsDto = evento.getConfirmacoes().stream().map(user -> userDetailsMapper.toDTO(user)).collect(Collectors.toSet());
            return confirmsDto;
        } catch (Exception e) {
            throw new RuntimeException("Confirmações não retornadas " + e.getMessage());
        }
    }

    public void removeConfirms (long event_id, long user_id){
        EventoEntity evento = eventoRepository.findById(event_id).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        UserEntity confirm = evento.getConfirmacoes().stream().filter((a)-> a.getId() == user_id).findFirst().orElseThrow(()->new EventNotFounded("Confirmação não encontrada"));

        try{
            evento.getConfirmacoes().remove(confirm);
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new EventoNotDeleted("Confirmação não deletada");
        }
    }
}
