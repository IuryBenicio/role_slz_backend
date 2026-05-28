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
    public ResponseEntity<EventDTOClass> createEvento(@RequestBody EventDTORequestClass eventoDTO){
        EventDTOClass evento = eventoService.createEvento(eventoDTO);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("get_many/{pagina}")
    public ResponseEntity<Set<EventoDTOWithIDClass>> getEventsPagination(@PathVariable("pagina") int pagina, @RequestParam("itens") int itens){
        Set<EventoDTOWithIDClass> response = eventoService.getEventsPagination(pagina, itens);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("changing_event_privacity/{id}")
    public ResponseEntity<String> changingEventPrivacity(@PathVariable("id") long id){
        eventoService.changePrivacity(id);
        return ResponseEntity.ok("Privacidade do evento alterada");
    }

    @PostMapping("add_category")
    public ResponseEntity<String> addCategoria(@RequestBody CategoryAddRequestDTO dto){
        eventoService.addCategoria(dto.eventoId(), dto.categoriaName());
        return ResponseEntity.status(202).body("Categoria adicionada");
    }

    @GetMapping("get_events_in_map_area")
    public ResponseEntity<Set<EventoDTOResponseDistanceClass>> getEventsInMapArea(@RequestBody GetsEventsInMapAreaDTO getsEventsInMapAreaDTO){
        Set<EventoDTOResponseDistanceClass> eventos = eventoService.getEventosInMapArea(getsEventsInMapAreaDTO.minLat(), getsEventsInMapAreaDTO.minLon(), getsEventsInMapAreaDTO.maxLat(), getsEventsInMapAreaDTO.maxLon());
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<EventDTOClass> getEventById(@PathVariable long id){
        EventDTOClass evento = eventoService.getEventoById(id);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("get_by_title")
    public ResponseEntity<EventDTOClass> getEventByTitle(@RequestParam String title){
        EventDTOClass evento = eventoService.getEventoByTitle(title);
        return ResponseEntity.ok(evento);
    }


    @PatchMapping("edit/{id}")
    public ResponseEntity<EventDTOClass> editEvent(@PathVariable long id, @RequestBody EventDTORequestClass eventoDTO){
        EventDTOClass evento = eventoService.editEvento(id, eventoDTO);
        return ResponseEntity.ok(evento);
    }

    @PatchMapping("edit_price/{id}")
    public ResponseEntity<NewPriceDTO> editPrice(@PathVariable long id, @RequestBody NewPriceDTO newPriceDTO){
        eventoService.editPrice(id, newPriceDTO);
        return ResponseEntity.ok(newPriceDTO);
    }

    @GetMapping("get_confirms/{id}")
    public ResponseEntity<Set<UserDTODetails>> getConfirms(@PathVariable long id){
        Set<UserDTODetails> confirms = eventoService.getConfirms(id);
        return ResponseEntity.ok(confirms);
    }

    @PatchMapping("remove_confirm/{event_id}/user/{user_id}")
    public ResponseEntity<String> removeConfirm(@PathVariable long event_id, @PathVariable long user_id){
        eventoService.removeConfirms(event_id, user_id);
        return ResponseEntity.status(202).body("Confirmação removida");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id){
        eventoService.deleteEvento(id);
        return ResponseEntity.status(HttpStatus.OK).body("Evento deletado");
    }
}
