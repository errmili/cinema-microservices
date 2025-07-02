package com.cinema.booking.application.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import com.cinema.booking.domain.models.Ticket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {

    private Long id;

    @NotNull(message = "Le nom du client est obligatoire.")
    private String nomClient;

    @NotNull(message = "Le code de paiement est obligatoire.")
    private String codePaiement;

    @NotNull(message = "Le prix est obligatoire.")
    private Double prix;

    @NotNull(message = "L'ID de la projection est obligatoire.")
    private Long projectionId;

    @NotNull(message = "L'ID de la place est obligatoire.")
    private Long placeId;

    private boolean reserve;  // exposé côté API

    public Ticket toEntity() {
        return Ticket.builder()
                .id(this.id)
                .nomClient(this.nomClient)
                .codePaiement(this.codePaiement)
                .prix(this.prix)
                .projectionId(this.projectionId)
                .placeId(this.placeId)
                .reserve(this.reserve)
                .build();
    }

    public static TicketDTO fromEntity(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .nomClient(ticket.getNomClient())
                .codePaiement(ticket.getCodePaiement())
                .prix(ticket.getPrix())
                .projectionId(ticket.getProjectionId())
                .placeId(ticket.getPlaceId())
                .reserve(ticket.isReserve())
                .build();
    }
}