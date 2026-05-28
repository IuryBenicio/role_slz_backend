package com.example.roleslz_backend.Tables.events.Projections;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.users.DTOS.UserSummaryDTO;
import org.locationtech.jts.geom.Point;

import java.util.Set;

public interface EventHeatPoint {
    long getId();
    UserSummaryDTO getOrganizador();
    EventDTOSumary getEventType();
    EventDTOSumary getEventPrivacity();
    Set<UserSummaryDTO> getConfirmacoes();
    Point getLocalizacao();
    Double getDistancia_metros();
}
