package com.cinema.movies.handler;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cinema.movies.exception.BusinessException;
import com.cinema.movies.exception.EntityCreationException;
import com.cinema.movies.exception.ObjectValidationException;
import com.cinema.movies.exception.OperationNotPermittedException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException exp) {
        // Créer une réponse d'exception avec les informations de BusinessErrorCodes
        ExceptionResponse response = ExceptionResponse.builder()
                .businessErrorCode(exp.getErrorCode())
                .error(exp.getErrorDescription())
                .build();

        return new ResponseEntity<>(response, exp.getHttpStatus());
    }

    @ExceptionHandler(ObjectValidationException.class)
    public ResponseEntity<ExceptionResponse> handleObjectValidationException(ObjectValidationException exp) {
        // Récupérer les violations de validation
        Set<String> errors = exp.getViolations();

        // Construire la réponse d'exception avec les erreurs de validation
        ExceptionResponse response = ExceptionResponse.builder()
                .error("Validation Error")
                .validationErrors(errors)  // Ajouter les erreurs de validation
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EntityCreationException.class)
    public ResponseEntity<ExceptionResponse> handleEntityCreationException(EntityCreationException exp) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // Retourner un code HTTP 400 pour une erreur de création d'entité
                .body(
                        ExceptionResponse.builder()
                                .error("Entity Creation Error: " + exp.getMessage()) // Message d'erreur spécifique à la création d'entité
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            var errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        ExceptionResponse response = ExceptionResponse.builder()
                .error("Validation Error")
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal error, please contact the admin")
                                .error(exp.getMessage())
                                .build()
                );
    }
}
