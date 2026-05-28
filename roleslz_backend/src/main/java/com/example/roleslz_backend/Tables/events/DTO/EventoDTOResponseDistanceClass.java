package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDTOResponseDistanceClass {
    private long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String enderecoExtenso;
    private Long spotId;
    private String spotNome;
    private long organizadorId;
    private String organizadorNome;
    private String imageUrl;
    private EstadoEvento estadoEvento;
    private Integer confirmacoes;
    private double lat;
    private double lng;
    private double distanciaMetros;
}
