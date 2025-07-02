package com.cinema.movies.clients.ticket;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
        name = "booking-service",
        url = "${application.config.ticket-url}"
)
public interface TicketClient {

    @GetMapping("/isReserved")
    boolean isPlaceReserved(@RequestParam("placeId") Long placeId);
}