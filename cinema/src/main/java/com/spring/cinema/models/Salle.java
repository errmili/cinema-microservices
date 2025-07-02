package com.spring.cinema.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salle extends AbstractEntity {

    private String name;
//    private int nombrePlaces;

    @ManyToOne(cascade = CascadeType.ALL)
    private Cinema cinema;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL)
    private List<Place> places;
}