package com.example.roleslz_backend.Tables.events.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTOSumary {
    @NotNull
    long Id;
    @NotBlank
    String title;
    @NotBlank String description;
    @NotNull
    LocalDateTime startDate;
    @NotBlank String imageUrl;
    @NotNull
    BigDecimal price;
    Double latitude;
    Double longitude;
}
