package com.example.roleslz_backend.Tables.categoria.DTO;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTOClass {
    private long id;

    private String name;

    private Set<EventoEntity> eventos;
}
