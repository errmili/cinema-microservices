package com.cinema.booking.infrastructure.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.infrastructure.controllers.api.TicketApi;
import com.cinema.booking.application.dto.TicketDTO;
import com.cinema.booking.domain.services.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
public class TicketController implements TicketApi {

    private TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public TicketDTO save(@RequestBody TicketDTO ticketDTO) {
        return ticketService.save(ticketDTO);
    }

    @Override
    public TicketDTO findById(@PathVariable("idTicket") Long idTicket) {
        return ticketService.getById(idTicket);
    }

    @Override
    public List<TicketDTO> findAll() {
        return ticketService.getAll();
    }

    @Override
    public void delete(@PathVariable("idTicket") Long idTicket) {
        ticketService.delete(idTicket);
    }

    @GetMapping("reservations/v1/tickets/isReserved")
    public ResponseEntity<Boolean> isPlaceReserved(@RequestParam Long placeId, @RequestParam Long projectionId) {
        boolean reserved = ticketService.isPlaceReserved(placeId, projectionId);
        return ResponseEntity.ok(reserved);
    }
}
