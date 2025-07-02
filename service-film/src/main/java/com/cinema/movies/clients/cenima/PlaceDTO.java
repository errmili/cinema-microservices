package com.cinema.movies.clients.cenima;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PlaceDTO {

    private Long id;

    @NotNull
    private LocalDateTime heureDebut;

    @NotNull(message = "Le numéro de la place ne doit pas être vide.")
    private int numero;

    private double longitude;
    private double latitude;
    private double altitude;

    @NotNull(message = "L'id de la salle ne doit pas être vide.")
    private Long salleId;

    @NotNull(message = "L'id de la projection ne doit pas être vide.")
    private Long projectionId;
}