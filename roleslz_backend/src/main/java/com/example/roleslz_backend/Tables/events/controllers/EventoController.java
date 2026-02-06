package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.*;
import com.example.roleslz_backend.Tables.events.services.EventoService;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/evento")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("create")
    public ResponseEntity<?> createEvento(@RequestBody EventoDTORequest eventoDTO){
        EventoDTO evento = eventoService.createEvento(eventoDTO);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("get_events_around")
    public ResponseEntity<?> getEventsAround(@RequestBody GetEventsAroundDTO getEventsAroundDTO){
        Set<EventoDTOResponseDistance> eventos = eventoService.getEventsAround(getEventsAroundDTO.lat(), getEventsAroundDTO.lng(), getEventsAroundDTO.raioKm());

        return ResponseEntity.ok(eventos);
    }

    @GetMapping("get_events_in_map_area")
    public ResponseEntity<?> getEventsInMapArea(@RequestBody GetsEventsInMapAreaDTO getsEventsInMapAreaDTO){
        Set<EventoDTOResponseDistance> eventos = eventoService.getEventosInMapArea(getsEventsInMapAreaDTO.minLat(), getsEventsInMapAreaDTO.minLon(), getsEventsInMapAreaDTO.maxLat(), getsEventsInMapAreaDTO.maxLon());
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getEvent(@PathVariable long id){
        EventoDTO evento = eventoService.getEvento(id);
        return ResponseEntity.ok(evento);
    }


    @PatchMapping("edit/{id}")
    public ResponseEntity<?> editEvent(@PathVariable long id, @RequestBody EventoDTO eventoDTO){
        EventoDTO evento = eventoService.editEvento(id, eventoDTO);
        return ResponseEntity.ok(evento);
    }

    @PatchMapping("edit_price/{id}")
    public ResponseEntity<?> editPrice(@PathVariable long id, @RequestBody NewPriceDTO newPriceDTO){
        eventoService.editPrice(id, newPriceDTO);
        return ResponseEntity.ok(newPriceDTO);
    }

    @GetMapping("get_confirms/{id}")
    public ResponseEntity<?> getConfirms(@PathVariable long id){
        Set<UserDTODetails> confirms = eventoService.getConfirms(id);
        return ResponseEntity.ok(confirms);
    }

    @PatchMapping("remove_confirm/{event_id}/user/{user_id}")
    public ResponseEntity<?> removeConfirm(@PathVariable long event_id, @PathVariable long user_id){
        return null;
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        eventoService.deleteEvento(id);
        return ResponseEntity.status(HttpStatus.OK).body("Evento deletado");
    }
}
