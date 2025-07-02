package com.spring.cinema.models;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entité abstraite pour gérer les propriétés communes de toutes les entités,
 * comme l'identifiant et les dates de création et de mise à jour.
 *
 *
 * Résumé des avantages de cette approche :
 * Réduction du code dupliqué : En héritant de AbstractEntity, les entités ne répètent pas les mêmes attributs (comme id et les dates).
 *
 * Lisibilité : L'utilisation de Lombok avec des annotations comme @Data et @Builder simplifie considérablement le code, ce qui le rend plus propre et plus facile à maintenir.
 *
 * Meilleure gestion des relations : Les relations entre entités sont clairement définies avec @ManyToOne et @JoinColumn.
 *
 * Validation des données : Avec @NotNull, tu t'assures que les champs nécessaires sont bien remplis avant d'être persistés.
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

    /**
     * Identifiant unique de l'entité.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date et heure de création de l'entité.
     * La date est automatiquement gérée par Spring Data JPA.
     */
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

}
