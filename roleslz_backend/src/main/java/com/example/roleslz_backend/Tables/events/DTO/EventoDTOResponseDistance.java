package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoDTOResponseDistance(
        @NotNull long id,
        @NotBlank String title,
        @NotBlank String description,
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate,
        @NotNull Point local,
        @NotBlank String enderecoExtenso,
        @NotNull UserEntity organizador,
        @NotBlank String imageUrl,
        @NotNull EstadoEvento estadoEvento,
        @NotNull BigDecimal price,
        //SPOT
        @NotBlank String lat,
        @NotBlank String lng,
        @NotNull Spring distanciaMetros
) {
}
