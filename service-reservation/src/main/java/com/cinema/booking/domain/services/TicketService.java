package com.cinema.booking.domain.services;

import java.util.List;

import com.cinema.booking.application.dto.TicketDTO;

public interface TicketService {
    TicketDTO save(TicketDTO dto);
    TicketDTO getById(Long id);
    List<TicketDTO> getAll();
    void delete(Long id);

    public boolean isPlaceReserved(Long placeId, Long idProjection);

}
