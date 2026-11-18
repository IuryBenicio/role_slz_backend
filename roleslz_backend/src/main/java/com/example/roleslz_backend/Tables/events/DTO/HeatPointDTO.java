package com.example.roleslz_backend.Tables.events.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Um ponto do heatmap: coordenada do spot + intensidade (quantidade de check-ins ativos ali).
// Formato consumido diretamente por libs de heatmap (Google Maps HeatmapLayer, Mapbox heatmap-layer, etc).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeatPointDTO {
    private Double lat;
    private Double lng;
    private Long peso;
}
