package com.example.roleslz_backend.Tables.users.DTOS.Req;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import com.example.roleslz_backend.Tables.users.entity.AuthProvider;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import java.util.List;
import java.util.Set;

public record UserEditDTO(String nome,
                          String sobrenome,
                          Sexo sexo,
                          String email,
                          Integer idade,
                          List<EventDTOSumary> eventos,
                          List<EventDTOSumary> historicoEventos,
                          String providerId,
                          AuthProvider authProvider,
                          Set<UserSummaryDTO> following,
                          Set<UserSummaryDTO> followers,
                          String fcmToken,
                          String image) { // Adicionado
}
