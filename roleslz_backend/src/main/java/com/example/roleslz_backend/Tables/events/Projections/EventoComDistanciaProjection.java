package com.example.roleslz_backend.Tables.events.Projections;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EventoComDistanciaProjection {
    long getId();
    String getTitle();
    String getDescription();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getEnderecoExtenso();
    UserEntity getOrganizador();
    String getImageUrl();
    EstadoEvento getEstadoEvento();
    BigDecimal getPrice();
    SpotEntity getSpot();
    //DISTANCIA ATRAVÉS DO POSTGIS
    Double getDistancia_metros();
}
