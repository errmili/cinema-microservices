package com.spring.cinema.handler;

import lombok.Getter;

import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    // Erreurs de recherche d'entité
    CINEMA_NOT_FOUND(6001, HttpStatus.NOT_FOUND, "Cinema not found"),
    CINEMA_CREATION_FAILED(6002, HttpStatus.BAD_REQUEST, "Failed to create cinema"),
    INVALID_CINEMA_ID(6003, HttpStatus.BAD_REQUEST, "Invalid cinema ID"),



    // Erreurs de gestion des salles
    SALLE_NOT_FOUND(8001, HttpStatus.NOT_FOUND, "Salle not found"),
    SALLE_CREATION_FAILED(8002, HttpStatus.BAD_REQUEST, "Failed to create salle"),
    INVALID_SALLE_ID(8003, HttpStatus.BAD_REQUEST, "Invalid salle ID"),



    // Erreurs de gestion des villes
    VILLE_NOT_FOUND(9001, HttpStatus.NOT_FOUND, "Ville not found"),
    VILLE_CREATION_FAILED(9002, HttpStatus.BAD_REQUEST, "Failed to create ville"),
    INVALID_VILLE_ID(9003, HttpStatus.BAD_REQUEST, "Invalid ville ID"),



    // Erreurs de gestion des places
    PLACE_NOT_FOUND(7001, HttpStatus.NOT_FOUND, "Place not found"),
    PLACE_CREATION_FAILED(7002, HttpStatus.BAD_REQUEST, "Failed to create place"),
    INVALID_PLACE_ID(7003, HttpStatus.BAD_REQUEST, "Invalid place ID"),


    // Erreurs de données invalides
    INVALID_CINEMA_DATA(3001, HttpStatus.BAD_REQUEST, "Invalid cinema data provided"),
    INVALID_SALLE_DATA(3002, HttpStatus.BAD_REQUEST, "Invalid salle data provided"),
    INVALID_PLACE_DATA(3003, HttpStatus.BAD_REQUEST, "Invalid place data provided"),

    // Erreur interne du serveur
    SERVER_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred");

    @Getter
    private final int code;
    @Getter
    private final HttpStatus httpStatus;
    @Getter
    private final String description;

    // Constructeur
    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.httpStatus = status;
        this.description = description;
    }
}
