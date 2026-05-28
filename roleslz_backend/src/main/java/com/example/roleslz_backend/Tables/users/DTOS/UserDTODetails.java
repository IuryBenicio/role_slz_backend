package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Set;

public record UserDTODetails(
        @NotBlank String nome,
        @NotBlank String sobrenome,
        @NotBlank Sexo sexo,
        @NotBlank String email,
        @NotBlank Integer idade,
        @NotNull List<EventDTOSumary> eventos,
        @NotNull List<EventDTOSumary> historicoEventos,
        Set<UserSummaryDTO> following,
        Set<UserSummaryDTO> followers,
        @NotNull String fcmToken) {
}
