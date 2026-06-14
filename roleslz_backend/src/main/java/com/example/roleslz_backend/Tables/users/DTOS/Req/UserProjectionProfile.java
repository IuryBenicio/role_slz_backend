package com.example.roleslz_backend.Tables.users.DTOS.Req;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.entity.Sexo;

import java.util.List;

public interface UserProjectionProfile {
    long getId();
    String getNome();
    String getSobrenome();
    Sexo getSexo();
    String getEmail();
    Integer getIdade();
    List<EventDTOSumary> getEventos();
    List<EventDTOSumary> getHistoricoEventos();
}
