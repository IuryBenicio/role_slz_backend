package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetailsWithId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.math.BigDecimal;
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
    private Point local;
    private String enderecoExtenso;
    private UserDTODetailsWithId organizador;
    private String imageUrl;
    private EstadoEvento estadoEvento;
    private BigDecimal price;
    private double lat;
    private double lng;
    private double distanciaMetros;
}
