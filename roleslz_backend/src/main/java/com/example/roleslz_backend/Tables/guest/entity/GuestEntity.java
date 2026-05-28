package com.example.roleslz_backend.Tables.guest.entity;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "guest")
public class GuestEntity {
    @UUID
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private EventoEntity evento;

}
