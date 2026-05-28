package com.example.roleslz_backend.Tables.categoria.DTO;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTORequestClass {
    long id;
    @NotBlank String name;
    Set<EventDTOSumary> eventos;
}
