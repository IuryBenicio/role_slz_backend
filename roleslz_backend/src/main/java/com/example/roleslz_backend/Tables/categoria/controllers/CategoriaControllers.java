package com.example.roleslz_backend.Tables.categoria.controllers;

import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTORequestClass;
import com.example.roleslz_backend.Tables.categoria.DTO.CategoriaDTOResponse;
import com.example.roleslz_backend.Tables.categoria.services.CategoriaServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categoria")
public class CategoriaControllers {

    private CategoriaServices categoriaServices;

    public CategoriaControllers(CategoriaServices categoriaServices) {
        this.categoriaServices = categoriaServices;
    }

    @PostMapping("add_category")
    ResponseEntity<CategoriaDTOResponse> addCategory(@RequestBody CategoriaDTORequestClass dto){
        CategoriaDTOResponse response = categoriaServices.addCategoria(dto);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("delete_category/{id}")
    ResponseEntity<String> deleteCategory(@PathVariable long id){
        categoriaServices.deleteCategoria(id);
        return ResponseEntity.status(200).body("Categoria deletada com sucesso");
    }

}
