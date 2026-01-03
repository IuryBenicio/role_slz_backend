package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.geo.Point;

import java.util.List;

public record UserDTODetails(
        @NotBlank String nome,
        @NotBlank String sobrenome,
        @NotBlank Sexo sexo,
        @NotBlank String email,
        @NotBlank Integer idade,
        @NotNull List<EventoEntity> eventos,
        @NotNull List<EventoEntity> historicoEventos
        ) {
}
