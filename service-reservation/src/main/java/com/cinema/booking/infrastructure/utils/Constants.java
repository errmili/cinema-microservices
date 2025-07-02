package com.cinema.booking.infrastructure.utils;

public interface Constants {

    // URL de base de l'API
    String APP_ROOT = "reservations/v1";

    // Endpoints pour Ticket
    String TICKET_ENDPOINT = APP_ROOT + "/tickets";
    String CREATE_TICKET_ENDPOINT = TICKET_ENDPOINT + "/create";
    String FIND_TICKET_BY_ID_ENDPOINT = TICKET_ENDPOINT + "/{idTicket}";
    String FIND_ALL_TICKETS_ENDPOINT = TICKET_ENDPOINT + "/all";
    String DELETE_TICKET_ENDPOINT = TICKET_ENDPOINT + "/delete/{idTicket}";
}
