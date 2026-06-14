package com.example.roleslz_backend.Tables.events.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotosEventoDTORes {
    private long id;
    private String link;
    private EventDTOSumary evento;
}
