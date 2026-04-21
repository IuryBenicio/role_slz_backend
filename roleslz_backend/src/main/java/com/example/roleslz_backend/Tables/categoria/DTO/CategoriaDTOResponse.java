package com.example.roleslz_backend.Tables.categoria.DTO;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTOWithID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTOResponse {
    private long id;

    private String name;

    private Set<EventoDTOWithID> eventos;
}
