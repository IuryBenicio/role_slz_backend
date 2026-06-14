package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    List<PhotosEventoSummaryDTO> images;
    @NotNull
    BigDecimal price;
    Double latitude;
    Double longitude;

    public static EventDTOSumary toSummaryDTO(EventoEntity entity) {
        if (entity == null) return null;
        return new EventDTOSumary(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getStartDate(),
                entity.getImages().stream().map(PhotosEventoSummaryDTO::toSummaryDTO).toList(),
                entity.getPrice(), entity.getSpot().getLocalizacao().getY(), entity.getSpot().getLocalizacao().getX());
    }
}
