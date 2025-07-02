package com.spring.cinema.dto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.spring.cinema.models.Cinema;
import com.spring.cinema.models.Ville;
import com.spring.cinema.repository.VilleRepository;

@Data
public class CinemaDTO {

    private Long id;

    @NotNull(message = "Le nom du cinéma ne doit pas être vide.")
    @Size(min = 1, message = "Le nom du cinéma ne doit pas être vide.")
    private String name;

    @NotNull(message = "La longitude du cinéma ne doit pas être vide.")
    private double longitude;

    @NotNull(message = "La latitude du cinéma ne doit pas être vide.")
    private double latitude;

    private double altitude;

//    @Positive(message = "Le nombre de salles doit être un nombre positif.")
//    private int nombreSalles;

    @NotNull(message = "L'id de la ville ne doit pas être vide.")
    private Long villeId; // Utilisation de l'id de la ville

    public Cinema toEntity(VilleRepository villeRepository) {
        Cinema cinema = new Cinema();
        cinema.setId(this.id);
        cinema.setName(this.name);
        cinema.setLongitude(this.longitude);
        cinema.setLatitude(this.latitude);
        cinema.setAltitude(this.altitude);
        //cinema.setNombreSalles(this.nombreSalles);
        // Le set de la ville ici peut être mappé si nécessaire
        // cinema.setVille(new Ville(this.villeId));
        // Mappage de l'id ville
        Ville ville = villeRepository.findById(this.villeId)
                .orElseThrow(() -> new EntityNotFoundException("ville not found with id " + this.villeId));
        cinema.setVille(ville);
        return cinema;
    }

    public static CinemaDTO fromEntity(Cinema cinema) {
        CinemaDTO dto = new CinemaDTO();
        dto.setId(cinema.getId());
        dto.setName(cinema.getName());
        dto.setLongitude(cinema.getLongitude());
        dto.setLatitude(cinema.getLatitude());
        dto.setAltitude(cinema.getAltitude());
       // dto.setNombreSalles(cinema.getNombreSalles());
        dto.setVilleId(cinema.getVille() != null ? cinema.getVille().getId() : null);
        return dto;
    }
}