package com.spring.cinema.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cinema extends AbstractEntity {

    private String name;
    private double longitude;
    private double latitude;
    private double altitude;
    //private int nombreSalles;

    @ManyToOne(cascade = CascadeType.ALL)
    private Ville ville;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    private List<Salle> salles;
}