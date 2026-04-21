package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UserDTODetailsWithId {
    @NotNull long id;
    @NotBlank String nome;
    @NotBlank String sobrenome;
    @NotBlank Sexo sexo;
    @NotBlank String email;
    @NotBlank Integer idade;
    @NotNull List<EventoDTO> eventos;
    @NotNull List<EventoDTO> historicoEventos;
    @NotNull String fcmToken;
}
