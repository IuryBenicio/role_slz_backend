package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.services.PresencaAtivaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
public class PresencaAtivaController {

    private final PresencaAtivaService presencaAtivaService;

    public PresencaAtivaController(PresencaAtivaService presencaAtivaService) {
        this.presencaAtivaService = presencaAtivaService;
    }

    // Disparado quando o SO detecta a entrada na cerca
    @PostMapping("/{eventoId}")
    public ResponseEntity<Void> entrarNoEvento(@PathVariable Long eventoId, @RequestAttribute("userId") Long userId) {
        presencaAtivaService.processarCheckin(userId, eventoId);
        return ResponseEntity.ok().build();
    }

    // Disparado quando o SO detecta a saída da cerca
    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Void> sairDoEvento(@PathVariable Long eventoId, @RequestAttribute("userId") Long userId) {
        presencaAtivaService.processarCheckout(userId, eventoId);
        return ResponseEntity.noContent().build();
    }
}