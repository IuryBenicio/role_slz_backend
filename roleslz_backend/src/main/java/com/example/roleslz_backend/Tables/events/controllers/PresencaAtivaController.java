package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.CheckinRequestDTO;
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

    // Chamado pelo app DEPOIS que a permanência mínima na cerca foi cumprida.
    // O corpo traz o instante da entrada; o service recusa o que não bate com o Dwell Time.
    @PostMapping("/{eventoId}")
    public ResponseEntity<Void> entrarNoEvento(@PathVariable Long eventoId,
                                               @RequestBody(required = false) CheckinRequestDTO dto) {
        presencaAtivaService.processarCheckin(
                userUtils.getUserLogado().getId(),
                eventoId,
                dto == null ? null : dto.entradaEmEpochMs());
        return ResponseEntity.ok().build();
    }

    // Disparado quando o SO detecta a saída da cerca
    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Void> sairDoEvento(@PathVariable Long eventoId) {
        presencaAtivaService.processarCheckout(userUtils.getUserLogado().getId(), eventoId);
        return ResponseEntity.noContent().build();
    }
}
