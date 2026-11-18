package com.example.roleslz_backend.Tables.events.entity;

/**
 * Como o registro permanente de participação num evento foi criado.
 */
public enum OrigemParticipacao {
    /** Geofence do celular detectou a pessoa dentro do local durante a janela do evento. */
    GEOFENCE,
    /** Pessoa tocou no botão "Cheguei no rolê!" durante o evento (com checagem de distância). */
    MANUAL,
    /** Pessoa respondeu "sim, eu fui" no prompt pós-evento (não verificado por GPS). */
    DECLARADO
}
