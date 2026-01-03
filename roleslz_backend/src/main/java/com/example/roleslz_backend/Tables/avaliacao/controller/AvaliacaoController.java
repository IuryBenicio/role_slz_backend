package com.example.roleslz_backend.Tables.avaliacao.controller;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.service.AvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rate")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    //controller
    @PostMapping("new_rate")
    public ResponseEntity<?> addNewRate(@RequestBody AvaliacaoDTO avaliacaoDTO){
        AvaliacaoDTO avaliacao = avaliacaoService.addAvaliacao(avaliacaoDTO, avaliacaoDTO.user().getId());
        return ResponseEntity.ok(avaliacao);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteRate(@PathVariable long id){
        avaliacaoService.deleteAvaliacao(id);
        return ResponseEntity.status(HttpStatus.OK).body("Avaliação deletada com sucesso");
    }

    @PatchMapping("edit/{id}")
    public ResponseEntity<?> editRate(@PathVariable long id, @RequestBody AvaliacaoDTO avaliacaoDTO){
        AvaliacaoDTO avaliacao = avaliacaoService.editAvaliacao(id, avaliacaoDTO.nota());
        return ResponseEntity.ok(avaliacao);
    }
}
