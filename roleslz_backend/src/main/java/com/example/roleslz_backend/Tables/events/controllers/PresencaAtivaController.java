package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.services.PresencaAtivaService;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
public class PresencaAtivaController {

    private final PresencaAtivaService presencaAtivaService;
    private final UserUtils userUtils;

    public PresencaAtivaController(PresencaAtivaService presencaAtivaService, UserUtils userUtils) {
        this.presencaAtivaService = presencaAtivaService;
        this.userUtils = userUtils;
    }

    // Disparado quando o SO detecta a entrada na cerca
    @PostMapping("/{eventoId}")
    public ResponseEntity<Void> entrarNoEvento(@PathVariable Long eventoId) {
        presencaAtivaService.processarCheckin(userUtils.getUserLogado().getId(), eventoId);
        return ResponseEntity.ok().build();
    }

    // Disparado quando o SO detecta a saída da cerca
    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Void> sairDoEvento(@PathVariable Long eventoId) {
        presencaAtivaService.processarCheckout(userUtils.getUserLogado().getId(), eventoId);
        return ResponseEntity.noContent().build();
    }
}
