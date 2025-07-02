package com.cinema.booking.application.mapper;

import com.cinema.booking.application.dto.TicketDTO;
import com.cinema.booking.domain.models.Ticket;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketDTO toDTO(Ticket ticket);
    Ticket toEntity(TicketDTO ticketDTO);
}
