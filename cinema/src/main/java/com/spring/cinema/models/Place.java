package com.spring.cinema.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place extends AbstractEntity {

    @NotNull
    private LocalDateTime heureDebut; // L'heure de début de la séance

    private int numero;
    private double longitude;
    private double latitude;
    private double altitude;

    @ManyToOne(cascade = CascadeType.ALL)
    private Salle salle;

    // Nouvelle propriété pour référence à la projection distante (sans relation JPA)
    //private Long projectionId;
}
