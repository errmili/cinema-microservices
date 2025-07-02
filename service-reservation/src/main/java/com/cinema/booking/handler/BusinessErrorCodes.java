package com.cinema.booking.handler;

import lombok.Getter;

import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    TICKET_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "Ticket not found with the given ID"),
    TICKET_CREATION_FAILED(4002, HttpStatus.BAD_REQUEST, "Failed to create the ticket"),
    INVALID_TICKET_DATA(4003, HttpStatus.BAD_REQUEST, "Invalid ticket data provided"),

    PLACE_NOT_FOUND(4004, HttpStatus.NOT_FOUND, "Associated place not found"),
    PROJECTION_NOT_FOUND(4005, HttpStatus.NOT_FOUND, "Associated projection not found"),

    SERVER_ERROR(4999, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred");

    @Getter
    private final int code;

    @Getter
    private final HttpStatus httpStatus;

    @Getter
    private final String description;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
