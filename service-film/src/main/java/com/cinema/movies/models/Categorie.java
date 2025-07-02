package com.cinema.movies.models;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie extends AbstractEntity {

    @NotNull
    private String nom;

    @OneToMany(mappedBy = "categorie")
    private List<Film> films;
}