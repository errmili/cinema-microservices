package com.cinema.booking.infrastructure.validators;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.cinema.booking.application.exception.ObjectValidationException;

//import com.spring.banking.exceptions.ObjectValidationException;

/**
 * @author Jaouad err
 * @since 15.09.24
 */
@Component
public class ObjectsValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        if (!violations.isEmpty()) {
            Set<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
           // throw new ObjectValidationException(errorMessages, objectToValidate.getClass().getName()); appel first constructor

            //seconde constructor
            throw new ObjectValidationException(
                    "Les données de l'objet sont invalides.", // Message explicite
                    errorMessages,                             // Violations
                    objectToValidate.getClass().getName()      // Source de la violation
            );
        }
    }
}