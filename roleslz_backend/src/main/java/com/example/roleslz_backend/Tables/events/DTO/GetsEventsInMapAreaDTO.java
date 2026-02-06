package com.example.roleslz_backend.Tables.events.DTO;

import jakarta.validation.constraints.NotNull;

public record GetsEventsInMapAreaDTO(
        @NotNull double minLat, @NotNull double minLon, @NotNull double maxLat, @NotNull double maxLon
) {
}
