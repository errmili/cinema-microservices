package com.spring.cinema.clients.ticket;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import com.spring.cinema.clients.interceptor.FeignInterceptorConfig;


@FeignClient(
        name = "booking-service",
        url = "${application.config.ticket-url}"
 //       configuration = FeignInterceptorConfig.class
)
public interface TicketClient {

    @GetMapping("/isReserved")
    boolean isPlaceReserved(@RequestParam("placeId") Long placeId, @RequestParam("projectionId") Long projectionId);
}