package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.DTO.PaginationWraper;
import com.example.roleslz_backend.Tables.events.services.ParticipacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participacao")
public class ParticipacaoController {

    private final ParticipacaoService participacaoService;

    public ParticipacaoController(ParticipacaoService participacaoService) {
        this.participacaoService = participacaoService;
    }

    // Resposta "sim, eu fui" ao prompt pós-evento.
    @PatchMapping("confirmar/{eventoId}")
    public ResponseEntity<String> confirmarPresenca(@PathVariable long eventoId) {
        participacaoService.confirmarPresencaDeclarada(eventoId);
        return ResponseEntity.ok("Presença confirmada no evento " + eventoId + ".");
    }

    // Histórico de eventos que o usuário logado frequentou.
    @GetMapping("historico")
    public ResponseEntity<PaginationWraper> getHistorico(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int itens) {
        return ResponseEntity.ok(participacaoService.getHistorico(pagina, itens));
    }

    // Eventos encerrados aguardando a resposta "você foi?" (card in-app).
    @GetMapping("pendentes")
    public ResponseEntity<List<EventDTOSumary>> getPendentes() {
        return ResponseEntity.ok(participacaoService.getPendentesConfirmacao());
    }
}
