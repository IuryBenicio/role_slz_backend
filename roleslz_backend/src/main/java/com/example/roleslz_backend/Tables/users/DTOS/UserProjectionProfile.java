package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.entity.Sexo;

import java.util.List;

public record UserProjectionProfile(
    long id,
    String nome,
    String sobrenome,
    Sexo sexo,
    String email,
    Integer idade,
    List<EventDTOSumary> eventos,
    List<EventDTOSumary> historicoEventos
) {
}
