package com.cinema.movies.dto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

import com.cinema.movies.models.Film;
import com.cinema.movies.models.Projection;
import com.cinema.movies.models.Seance;
import com.cinema.movies.repository.FilmRepository;
import com.cinema.movies.repository.SeanceRepository;

@Data
public class ProjectionDTO {

    private Long id;


    @NotNull(message = "La date de la projection ne doit pas être vide.")
    @FutureOrPresent(message = "La projection doit être dans le futur ou présente.")
    private LocalDateTime date; // La date de la projection

    @NotNull(message = "Le prix de la projection ne doit pas être vide.")
    @Positive(message = "Le prix doit être un nombre positif.")
    private Double prix; // Prix de la projection

    @NotNull(message = "L'id du film ne doit pas être vide.")
    @Min(value = 1, message = "L'id du film doit être supérieur ou égal à 1.")
    private Long filmId; // L'id du film associé

    @NotNull(message = "L'id de la séance ne doit pas être vide.")
    @Min(value = 1, message = "L'id de la séance doit être supérieur ou égal à 1.")
    private Long seanceId; // L'id de la séance associée

    @NotNull(message = "L'id de la salle ne doit pas être vide.")
    private Long salleId;

    // Convertir ProjectionDTO en Projection (toEntity)
    public Projection toEntity(SeanceRepository seanceRepository, FilmRepository filmRepository) {
        Projection projection = new Projection();
        projection.setId(this.id);
        projection.setDate(this.date);
        projection.setPrix(this.prix);
        // filmId et seanceId peuvent être mappés ici ou dans le service

        // Mappage de l'id film
        Film film = filmRepository.findById(this.filmId)
                .orElseThrow(() -> new EntityNotFoundException("Film not found with id " + this.filmId));
        projection.setFilm(film);

        // Mappage de l'id seance
        Seance seance = seanceRepository.findById(this.seanceId)
                .orElseThrow(() -> new EntityNotFoundException("Seance not found with id " + this.seanceId));
        projection.setSeance(seance);

        // SalleId simple, pas besoin de fetch
        projection.setSalleId(this.salleId);

        return projection;
    }

    // Convertir Projection en ProjectionDTO (fromEntity)
    public static ProjectionDTO fromEntity(Projection projection) {
        ProjectionDTO dto = new ProjectionDTO();
        dto.setId(projection.getId());
        dto.setDate(projection.getDate());
        dto.setPrix(projection.getPrix());
        dto.setFilmId(projection.getFilm() != null ? projection.getFilm().getId() : null);
        dto.setSeanceId(projection.getSeance() != null ? projection.getSeance().getId() : null);
        dto.setSalleId(projection.getSalleId());
        return dto;
    }
}