package com.example.roleslz_backend.Tables.events.Projections;


import java.time.LocalDateTime;

public interface EventoComDistanciaProjection {

    // Dados Básicos do Evento
    Long getId();
    String getTitle();
     String getDescription();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getImageUrl();
    String getEstadoEvento();
    // Dados do Organizador (Achatado)
    Long getOrganizadorId();
    String getOrganizadorNome();
    // O Spot (Achatado)
    Long getSpotId();
    String getSpotNome();
    // O "Peso" para o Mapa)
    Integer getConfirmacoesCount();
    // Coordenadas Desmembradas
    Double getLat();
    Double getLng();
    // Distância calculada pelo PostGIS
    Double getDistancia_metros();
}
