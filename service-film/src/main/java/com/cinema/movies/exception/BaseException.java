package com.cinema.movies.exception;


import com.cinema.movies.handler.BusinessErrorCodes;

public class BaseException extends RuntimeException {

    // Code d'erreur spécifique à l'exception
    private final BusinessErrorCodes businessErrorCodes;

    // Constructeurs de la BaseException
    public BaseException(String message) {
        super(message);
        this.businessErrorCodes = null;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.businessErrorCodes = null;
    }

    public BaseException(String message, BusinessErrorCodes businessErrorCodes) {
        super(message);
        this.businessErrorCodes = businessErrorCodes;
    }

    public BaseException(String message, Throwable cause, BusinessErrorCodes businessErrorCodes) {
        super(message, cause);
        this.businessErrorCodes = businessErrorCodes;
    }

    // Getter pour obtenir le code d'erreur
    public BusinessErrorCodes getBusinessErrorCodes() {
        return businessErrorCodes;
    }
}
