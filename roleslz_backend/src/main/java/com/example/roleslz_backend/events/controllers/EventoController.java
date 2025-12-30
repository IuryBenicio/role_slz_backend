package com.example.roleslz_backend.events.controllers;

import com.example.roleslz_backend.events.DTO.EventoDTO;
import com.example.roleslz_backend.events.entity.EventoEntity;
import com.example.roleslz_backend.events.services.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evento")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("create")
    public ResponseEntity<?> createEvento(@RequestBody EventoEntity eventoEntity){
        return null;
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getEvent(@PathVariable long id){
        EventoDTO evento = eventoService.getEvento(id);
        return ResponseEntity.ok(evento);
    }

    @PatchMapping("edit/{id}")
    public ResponseEntity<?> editEvent(){
        return null;
    }

    @GetMapping("get_confirms/{id}")
    public ResponseEntity<?> getConfirms(){
        return null;
    }

    @PatchMapping("remove_confirm/{event_id}/user/{user_id}")
    public ResponseEntity<?> removeConfirm(@PathVariable long event_id, @PathVariable long user_id){
        return null;
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> delete(){
        return null;
    }


}
