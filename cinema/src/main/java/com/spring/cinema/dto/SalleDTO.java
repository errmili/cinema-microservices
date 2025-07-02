package com.spring.cinema.dto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import com.spring.cinema.models.Cinema;
import com.spring.cinema.models.Salle;
import com.spring.cinema.models.Ville;
import com.spring.cinema.repository.CinemaRepository;

@Data
public class SalleDTO {

    private Long id;

    @NotNull(message = "Le nom de la salle ne doit pas être vide.")
    private String name;

   // @Positive(message = "Le nombre de places doit être un nombre positif.")
    private Integer nombrePlaces;

    @NotNull(message = "L'id du cinéma ne doit pas être vide.")
    private Long cinemaId;

    public Salle toEntity(CinemaRepository cinemaRepository) {
        Salle salle = new Salle();
        salle.setId(this.id);
        salle.setName(this.name);
        //salle.setNombrePlaces(this.nombrePlaces);
        // salle.setCinema(new Cinema(this.cinemaId)); // Optionally link to Cinema
        Cinema cinema = cinemaRepository.findById(this.cinemaId)
                .orElseThrow(() -> new EntityNotFoundException("cinema not found with id " + this.cinemaId));
        salle.setCinema(cinema);
        return salle;
    }

    public static SalleDTO fromEntity(Salle salle) {
        SalleDTO dto = new SalleDTO();
        dto.setId(salle.getId());
        dto.setName(salle.getName());
       // dto.setNombrePlaces(salle.getNombrePlaces());
        dto.setCinemaId(salle.getCinema() != null ? salle.getCinema().getId() : null);
        return dto;
    }
}