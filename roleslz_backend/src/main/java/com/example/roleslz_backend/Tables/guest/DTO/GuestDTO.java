package com.example.roleslz_backend.Tables.guest.DTO;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {

    private String name;
    private String email;
    private EventDTOSumary evento;

}
