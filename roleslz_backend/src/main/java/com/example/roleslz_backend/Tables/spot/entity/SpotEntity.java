package com.example.roleslz_backend.Tables.spot.entity;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Entity
@Table(name = "spot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "geometry(Point, 4326)")
    @NotNull(message = "Localização precisa existir")
    private Point localizacao;

    @OneToMany(mappedBy = "spot")
    private List<EventoEntity> eventos;

}
