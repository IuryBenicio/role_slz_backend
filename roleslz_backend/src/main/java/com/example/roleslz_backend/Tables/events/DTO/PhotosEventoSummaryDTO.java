package com.example.roleslz_backend.Tables.events.DTO;

import com.example.roleslz_backend.Tables.events.entity.PhotosEventoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotosEventoSummaryDTO {
    private long id;
    private String link;

    public static PhotosEventoSummaryDTO toSummaryDTO(PhotosEventoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PhotosEventoSummaryDTO(entity.getId(), entity.getLink());
    }
}
