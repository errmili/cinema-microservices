package com.spring.cinema.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Builder(toBuilder = true)
public class Ville extends AbstractEntity {


    private String cityName;
    private double longitude;
    private double latitude;
    private double altitude;


    @OneToMany(mappedBy = "ville", cascade = CascadeType.ALL)
    private List<Cinema> cinemas;
}
