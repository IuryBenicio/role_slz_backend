package com.example.roleslz_backend.events.services;

import com.example.roleslz_backend.events.DTO.EventoDTO;
import com.example.roleslz_backend.events.entity.EventoEntity;
import com.example.roleslz_backend.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.events.mapper.EventoMapper;
import com.example.roleslz_backend.events.repository.EventoRepository;
import org.springframework.stereotype.Service;

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
        EventoEntity exists = eventoRepository.
    }
}
