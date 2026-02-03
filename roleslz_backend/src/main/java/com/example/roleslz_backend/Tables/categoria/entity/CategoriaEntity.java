package com.example.roleslz_backend.Tables.categoria;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "categoriaEvento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "categorias")
    @JoinColumn(name = "evento_id")
    private Set<EventoEntity> eventos;

}
