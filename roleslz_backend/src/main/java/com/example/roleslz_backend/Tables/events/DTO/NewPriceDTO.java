package com.example.roleslz_backend.Tables.events.DTO;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NewPriceDTO(
        @NotNull BigDecimal oldPrice,
        @NotNull BigDecimal newPrice
        ) {
}
