package com.cinema.movies.handler;
import org.springframework.http.HttpStatus;
import lombok.Getter;

public enum BusinessErrorCodes {
    FILM_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Film not found"),
    FILM_CREATION_FAILED(3002, HttpStatus.BAD_REQUEST, "Failed to create film"),
    INVALID_FILM_ID(3003, HttpStatus.BAD_REQUEST, "Invalid film ID"),


    CATEGORY_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_CREATION_FAILED(1002, HttpStatus.BAD_REQUEST, "Failed to create category"),
    INVALID_CATEGORY_ID(1003, HttpStatus.BAD_REQUEST, "Invalid category ID"),




    PROJECTION_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "Projection not found"),
    PROJECTION_CREATION_FAILED(4002, HttpStatus.BAD_REQUEST, "Failed to create projection"),
    INVALID_PROJECTION_ID(4003, HttpStatus.BAD_REQUEST, "Invalid projection ID"),



    SEANCE_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "Seance not found"),
    SEANCE_CREATION_FAILED(5002, HttpStatus.BAD_REQUEST, "Failed to create seance"),
    INVALID_SEANCE_ID(5003, HttpStatus.BAD_REQUEST, "Invalid seance ID"),



    INVALID_FILM_DATA(3001, HttpStatus.BAD_REQUEST, "Invalid film data provided"),
    INVALID_PROJECTION_DATA(3002, HttpStatus.BAD_REQUEST, "Invalid projection data provided"),
    INVALID_SEANCE_DATA(3003, HttpStatus.BAD_REQUEST, "Invalid seance data provided"),
    SERVER_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred");
/*
        FILM_NOT_FOUND : Lorsque l'ID d'un film est introuvable.

        CATEGORY_NOT_FOUND : Lorsqu'une catégorie n'est pas trouvée.

        PROJECTION_NOT_FOUND : Lorsque la projection avec l'ID spécifié n'est pas trouvée.

        SEANCE_NOT_FOUND : Lorsque la séance n'est pas trouvée.

        FILM_CREATION_FAILED : Si la création d'un film échoue.

        PROJECTION_CREATION_FAILED : Si la création d'une projection échoue.

        SEANCE_CREATION_FAILED : Si la création d'une séance échoue.

        INVALID_FILM_DATA, INVALID_PROJECTION_DATA, INVALID_SEANCE_DATA : Erreurs liées aux données invalides pour un film, une projection ou une séance.

        SERVER_ERROR : Une erreur interne du serveur qui est utilisée pour les erreurs non gérées.
 */
    @Getter
    private final int code;
    @Getter
    private final HttpStatus httpStatus;
    @Getter
    private final String description;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.httpStatus = status;
        this.description = description;
    }
}
