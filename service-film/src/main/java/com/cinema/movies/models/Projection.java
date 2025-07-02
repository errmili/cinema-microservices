package com.cinema.movies.models;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projection extends AbstractEntity {

    @NotNull
    private LocalDateTime date; // La date de la projection

    @NotNull
    private Double prix; // Prix de la projection

    /*
    Une projection peut effectivement être associée à plusieurs films, mais ce n'est pas nécessairement logique dans un cinéma classique,où une
    projection représente une séance de film unique (c'est-à-dire que, généralement, pendant une projection, il y a un seul film projeté à la fois).
     */
    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film; // Le film associé à la projection


    @OneToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance; // La séance associée à la projection (relation OneToOne)

    // Nouvelle propriété : l'id de la salle (sans relation JPA)
    private Long salleId;
}