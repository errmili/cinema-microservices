package com.spring.cinema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.spring.cinema.models.Ville;

@Data
public class VilleDTO {

    private Long id;

    @NotNull(message = "Le nom de la ville ne doit pas être vide.")
    private String cityName;

    @NotNull(message = "La longitude de la ville ne doit pas être vide.")
    private double longitude;

    @NotNull(message = "La latitude de la ville ne doit pas être vide.")
    private double latitude;

    private double altitude;

    public Ville toEntity() {
        Ville ville = new Ville();
        ville.setId(this.id);
        ville.setCityName(this.cityName);
        ville.setLongitude(this.longitude);
        ville.setLatitude(this.latitude);
        ville.setAltitude(this.altitude);
        return ville;
    }

    public static VilleDTO fromEntity(Ville ville) {
        VilleDTO dto = new VilleDTO();
        dto.setId(ville.getId());
        dto.setCityName(ville.getCityName());
        dto.setLongitude(ville.getLongitude());
        dto.setLatitude(ville.getLatitude());
        dto.setAltitude(ville.getAltitude());
        return dto;
    }
}