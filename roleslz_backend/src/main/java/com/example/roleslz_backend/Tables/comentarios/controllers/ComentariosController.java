package com.example.roleslz_backend.Tables.comentarios.controllers;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.services.ComentariosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comentarios")
public class ComentariosController {

    private final ComentariosService comentariosService;

    public ComentariosController(ComentariosService comentariosService) {
        this.comentariosService = comentariosService;
    }

    @PostMapping("add")
    public ResponseEntity<?> addComentario(@RequestBody ComentariosDTO comentariosDTO){
        ComentariosDTO comentario = comentariosService.addComentario(comentariosDTO);
        return ResponseEntity.ok(comentario);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteComentario(@PathVariable long id){
        comentariosService.deleteComentario(id);
        return ResponseEntity.status(HttpStatus.OK).body("Comentário deletado.");
    }

    @PatchMapping("edit/{id}")
    public ResponseEntity<?> editComentario(@PathVariable long id, @RequestBody ComentariosDTO comentariosDTO){
        ComentariosDTO comentario = comentariosService.editComentario(id, comentariosDTO);
        return ResponseEntity.ok(comentario);
    }
}
