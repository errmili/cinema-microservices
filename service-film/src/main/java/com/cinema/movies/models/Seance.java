package com.cinema.movies.models;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seance extends AbstractEntity {

    @NotNull
    private LocalDateTime heureDebut; // L'heure de début de la séance

    /*
     Reformulation des relations :
        Séance : Une séance a une durée fixe (exemple : de 14h00 à 16h00). Elle peut contenir plusieurs projections,
        mais chaque projection doit être entièrement contenue dans le créneau de la séance.

        Projection : Chaque projection a une durée spécifique, et elle doit commencer et finir dans le temps de la séance.
     */
    /*
     Une séance a une durée fixe (par exemple : de 14h00 à 16h00).
     */
    @OneToOne(mappedBy = "seance")
    private Projection projection; // Une seule projection par séance
}

