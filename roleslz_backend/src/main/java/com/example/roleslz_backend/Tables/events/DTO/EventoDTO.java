package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.Set;

public record EventoDTO(@NotBlank String title,
                        @NotBlank String description,
                        @NotBlank LocalDateTime startDate,
                        @NotBlank LocalDateTime endDate,
                        @NotBlank Point localization,
                        @NotBlank String enderecoExtenso,
                        @NotBlank UserEntity organizador,
                        @NotBlank Set<UserEntity> confirmacoes) {
}
