package com.cinema.booking.application.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorDescription;

    public BusinessException(int errorCode, HttpStatus httpStatus, String errorDescription) {
        super(errorDescription);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}

