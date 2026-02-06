package com.example.roleslz_backend.Tables.events.DTO;

import jakarta.validation.constraints.NotNull;

public record GetEventsAroundDTO(
        @NotNull double lat,
        @NotNull double lng,
        @NotNull double raioKm
) {
}
