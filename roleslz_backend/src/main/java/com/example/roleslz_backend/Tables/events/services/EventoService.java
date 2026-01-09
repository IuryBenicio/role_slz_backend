package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.DTO.NewPriceDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.*;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapper;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapper;
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
    private final UserDetailsMapper userDetailsMapper;

    public EventoService(EventoRepository eventoRepository, EventoMapper eventoMapper, UserDetailsMapper userDetailsMapper) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
        this.userDetailsMapper = userDetailsMapper;
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
        Optional<EventoEntity> exists = eventoRepository.findByTitle(eventoDTO.title());
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
