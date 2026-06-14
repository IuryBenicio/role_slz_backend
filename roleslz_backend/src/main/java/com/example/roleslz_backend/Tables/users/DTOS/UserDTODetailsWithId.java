package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTODetailsWithId {
    @NotNull private long id;
    @NotBlank private String nome;
    @NotBlank private String sobrenome;
    @NotBlank private Sexo sexo;
    @NotBlank private String email;
    @NotBlank private Integer idade;
    private String image;
    @NotNull private List<EventDTOSumary> eventos;
    @NotNull private List<EventDTOSumary> historicoEventos;
    @NotNull private Set<UserSummaryDTO> following;
    @NotNull private Set<UserSummaryDTO> followers;
    @NotNull private String fcmToken;
}
