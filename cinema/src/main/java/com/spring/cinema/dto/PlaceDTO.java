package com.spring.cinema.dto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDateTime;

import com.spring.cinema.models.Place;
import com.spring.cinema.models.Salle;
import com.spring.cinema.repository.PlaceRepository;
import com.spring.cinema.repository.SaleRepository;

@Data
public class PlaceDTO {

    private Long id;

    @NotNull
    private LocalDateTime heureDebut;

    @NotNull(message = "Le numéro de la place ne doit pas être vide.")
    private int numero;

    private double longitude;
    private double latitude;
    private double altitude;

    @NotNull(message = "L'id de la salle ne doit pas être vide.")
    private Long salleId;

//    @NotNull(message = "L'id de la projection ne doit pas être vide.")
//    private Long projectionId;

    // Nouveau champ pour indiquer si la place est réservée
    private boolean reserve;

    public Place toEntity(SaleRepository saleRepository) {
        Place place = new Place();
        place.setId(this.id);
        place.setHeureDebut(this.heureDebut);
        place.setNumero(this.numero);
        place.setLongitude(this.longitude);
        place.setLatitude(this.latitude);
        place.setAltitude(this.altitude);

        Salle salle = saleRepository.findById(this.salleId)
                .orElseThrow(() -> new EntityNotFoundException("Salle not found with id " + this.salleId));
        place.setSalle(salle);

       // place.setProjectionId(this.projectionId);

        return place;
    }

    public static PlaceDTO fromEntity(Place place) {
        PlaceDTO dto = new PlaceDTO();
        dto.setId(place.getId());
        dto.setHeureDebut(place.getHeureDebut());
        dto.setNumero(place.getNumero());
        dto.setLongitude(place.getLongitude());
        dto.setLatitude(place.getLatitude());
        dto.setAltitude(place.getAltitude());
        dto.setSalleId(place.getSalle() != null ? place.getSalle().getId() : null);
    //    dto.setProjectionId(place.getProjectionId());
        // Par défaut on peut mettre false ici (sera écrasé par fromEntityWithReservation)
        dto.setReserve(false);
        return dto;
    }
}
