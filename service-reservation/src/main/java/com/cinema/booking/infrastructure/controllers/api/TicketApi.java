package com.cinema.booking.infrastructure.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.cinema.booking.application.dto.TicketDTO;
import com.cinema.booking.infrastructure.utils.Constants;

import java.util.List;

public interface TicketApi {

    @Operation(
            summary = "Enregistrer un ticket",
            description = "Cette méthode permet d'enregistrer ou de modifier un ticket"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le ticket a été créé/modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Le ticket n'est pas valide")
    })
    @PostMapping(value = Constants.CREATE_TICKET_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    TicketDTO save(@RequestBody TicketDTO ticketDTO);

    @Operation(
            summary = "Rechercher un ticket par ID",
            description = "Cette méthode permet de rechercher un ticket par son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le ticket a été trouvé dans la base de données"),
            @ApiResponse(responseCode = "404", description = "Aucun ticket trouvé avec cet ID")
    })
    @GetMapping(value = Constants.FIND_TICKET_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    TicketDTO findById(@PathVariable("idTicket") Long idTicket);

    @Operation(
            summary = "Liste des tickets",
            description = "Cette méthode permet d'obtenir tous les tickets enregistrés"
    )
    @ApiResponse(responseCode = "200", description = "Liste des tickets ou liste vide")
    @GetMapping(value = Constants.FIND_ALL_TICKETS_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<TicketDTO> findAll();

    @Operation(
            summary = "Supprimer un ticket",
            description = "Cette méthode permet de supprimer un ticket à partir de son ID"
    )
    @ApiResponse(responseCode = "200", description = "Le ticket a été supprimé")
    @DeleteMapping(value = Constants.DELETE_TICKET_ENDPOINT)
    void delete(@PathVariable("idTicket") Long id);
}