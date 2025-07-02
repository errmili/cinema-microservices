package com.cinema.booking.domain.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    /**
     * Date et heure de la dernière mise à jour de l'entité.
     * La date est automatiquement gérée par Spring Data JPA.
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    private String nomClient;

    private String codePaiement;

    private double prix;

    private Long projectionId; // relation à projection (ID distant)

    private Long placeId; // relation à place (ID distant)

    @Column(nullable = false)
    private boolean reserve = false;  // false par défaut (non réservé)
}