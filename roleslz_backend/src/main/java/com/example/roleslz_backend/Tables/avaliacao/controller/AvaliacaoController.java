package com.example.roleslz_backend.Tables.avaliacao.controller;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoRequestDTO;
import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoResponseDTO;
import com.example.roleslz_backend.Tables.avaliacao.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rate")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    // Autor vem do JWT, evento do path.
    @PostMapping("evento/{eventoId}")
    public ResponseEntity<AvaliacaoResponseDTO> avaliar(@PathVariable long eventoId,
                                                        @RequestBody @Valid AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(avaliacaoService.criarAvaliacao(eventoId, dto));
    }

    @PatchMapping("evento/{eventoId}")
    public ResponseEntity<AvaliacaoResponseDTO> editar(@PathVariable long eventoId,
                                                       @RequestBody @Valid AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(avaliacaoService.editarAvaliacao(eventoId, dto));
    }

    @DeleteMapping("evento/{eventoId}")
    public ResponseEntity<String> remover(@PathVariable long eventoId) {
        avaliacaoService.removerAvaliacao(eventoId);
        return ResponseEntity.ok("Avaliação removida.");
    }
}
