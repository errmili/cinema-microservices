package com.cinema.movies.models;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film extends AbstractEntity {

    private Long id; // Ajout temporaire pour tests ou builder

    @NotNull
    private String titre;

    private String description;

    @NotNull
    private Integer duree;  // Durée en minutes

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @OneToMany(mappedBy = "film")
    private List<Projection> projections;
}
