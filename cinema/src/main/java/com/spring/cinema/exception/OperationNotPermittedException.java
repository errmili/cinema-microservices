package com.spring.cinema.exception;

public class OperationNotPermittedException extends BaseException {

    public OperationNotPermittedException() {
        super("Opération non autorisée");
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}
