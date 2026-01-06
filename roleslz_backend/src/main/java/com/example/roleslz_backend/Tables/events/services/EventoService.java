package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventExists;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.exceptions.EventoNotDeleted;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapper;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    public EventoService(EventoRepository eventoRepository, EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
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

    public EventoDTO createEvento(EventoDTO eventoDTO){
        Optional<EventoEntity> exists = eventoRepository.findByName(eventoDTO.title());
        if(exists.isPresent()){
            throw new EventExists("Evento já existe");
        }

        if(eventoDTO.estadoEvento().equals(EstadoEvento.POS)){
            throw new EventExists("Evento com estado incorreto");
        }

        EventoEntity novoEvento = eventoMapper.toEntity(eventoDTO);

        EventoEntity evento = eventoRepository.save(novoEvento);

        return eventoMapper.toDTO(evento);
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
    }}
