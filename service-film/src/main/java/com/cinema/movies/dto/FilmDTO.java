package com.cinema.movies.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.cinema.movies.models.Categorie;
import com.cinema.movies.models.Film;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class FilmDTO {

    private Long id; // id du film, optionnel pour la création


    @NotNull(message = "Le titre du film ne doit pas être vide.")
    @Size(min = 1, message = "Le titre du film ne doit pas être vide.")
    private String titre;

    @Size(max = 500, message = "La description du film ne doit pas dépasser 500 caractères.")
    private String description;

    @NotNull(message = "La durée du film ne doit pas être vide.")
    @Positive(message = "La durée du film doit être un nombre positif.")
    private Integer duree;  // Durée en minutes

    @Size(max = 255, message = "L'URL de l'image ne doit pas dépasser 255 caractères.")
    private String imageUrl;

    //@JsonProperty("categorie_id")
    @NotNull(message = "L'id de la catégorie ne doit pas être vide.")
    private Long categorieId; // Utilisation de l'id de la catégorie au lieu de l'objet complet

    // Conversion de FilmDTO vers Film (toEntity)
    public Film toEntity() {
        
        Film film = new Film();
        film.setId(this.id); // uniquement si l'id est utilisé lors de la mise à jour
        film.setTitre(this.titre);
        film.setDescription(this.description);
        film.setDuree(this.duree);
        film.setImageUrl(this.imageUrl);
        // Le set de la catégorie (ici on passe juste l'id, tu peux récupérer la catégorie depuis un service si nécessaire)
        // film.setCategorie(new Categorie(this.categorieId)); // Id peut être mappé directement si on a la catégorie
        return film;
    }

    // Conversion de Film vers FilmDTO (fromEntity)
    public static FilmDTO fromEntity(Film film) {
        FilmDTO dto = new FilmDTO();
        dto.setId(film.getId());
        dto.setTitre(film.getTitre());
        dto.setDescription(film.getDescription());
        dto.setDuree(film.getDuree());
        dto.setImageUrl(film.getImageUrl());
        // On peut aussi remplir la catégorie ici si nécessaire
        dto.setCategorieId(film.getCategorie() != null ? film.getCategorie().getId() : null);
        return dto;
    }

}
