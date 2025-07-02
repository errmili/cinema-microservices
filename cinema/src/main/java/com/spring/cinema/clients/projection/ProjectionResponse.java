package com.spring.cinema.clients.projection;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectionResponse {

    private Long id;

    private LocalDateTime date; // La date de la projection

    private Double prix; // Prix de la projection

    private Long filmId; // L'id du film associé

    private Long seanceId; // L'id de la séance associée

    private Long salleId;
}
