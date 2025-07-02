package com.cinema.movies.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import com.cinema.movies.models.Categorie;

@Data
public class CategorieDTO {

    private Long id;

    @NotNull(message = "Le nom de la catégorie ne doit pas être vide.")
    @Size(min = 1, message = "Le nom de la catégorie ne doit pas être vide.")
    private String nom;

    // Convertir CategorieDTO en Categorie (toEntity)
    public Categorie toEntity() {

        // Vérification si le nom est null ou vide
//        if (this.nom == null || this.nom.trim().isEmpty()) {
//            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être null ou vide.");
//        }

        Categorie categorie = new Categorie();
        categorie.setId(this.id);
        categorie.setNom(this.nom);
        return categorie;
    }

    // Convertir Categorie en CategorieDTO (fromEntity)
    public static CategorieDTO fromEntity(Categorie categorie) {

        if (categorie == null) {
            throw new IllegalArgumentException("L'entité Categorie ne peut pas être nulle.");
        }

        CategorieDTO dto = new CategorieDTO();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        return dto;
    }

}