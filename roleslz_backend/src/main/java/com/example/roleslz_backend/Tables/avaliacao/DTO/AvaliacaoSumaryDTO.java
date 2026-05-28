package com.example.roleslz_backend.Tables.avaliacao.DTO;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoSumaryDTO {
    @NotBlank long id;
    @NotBlank float nota;
    @NotNull UserSummaryDTO user;
    @NotNull EventDTOSumary evento;
    @NotNull LocalDateTime createdAt;
}
