package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.services.EventoService;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
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
    public ResponseEntity<?> createEvento(@RequestBody EventoDTO eventoDTO){
        EventoDTO evento = eventoService.createEvento(eventoDTO);
        return ResponseEntity.ok(evento);
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
