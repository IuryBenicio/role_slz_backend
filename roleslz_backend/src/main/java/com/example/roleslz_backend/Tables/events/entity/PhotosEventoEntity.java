package com.example.roleslz_backend.Tables.events.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evento_galery")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotosEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private EventoEntity evento;

}
