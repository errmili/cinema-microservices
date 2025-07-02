package com.cinema.movies.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import com.cinema.movies.models.Seance;

@Data
public class SeanceDTO {

    private Long id;

    @NotNull(message = "L'heure de début de la séance ne doit pas être vide.")
    @FutureOrPresent(message = "L'heure de début doit être dans le futur ou présente.")
    private LocalDateTime heureDebut; // L'heure de début de la séance

    // Convertir SeanceDTO en Seance (toEntity)
    public Seance toEntity() {
        Seance seance = new Seance();
        seance.setId(this.id);
        seance.setHeureDebut(this.heureDebut);
        return seance;
    }

    // Convertir Seance en SeanceDTO (fromEntity)
    public static SeanceDTO fromEntity(Seance seance) {
        SeanceDTO dto = new SeanceDTO();
        dto.setId(seance.getId());
        dto.setHeureDebut(seance.getHeureDebut());
        return dto;
    }

}