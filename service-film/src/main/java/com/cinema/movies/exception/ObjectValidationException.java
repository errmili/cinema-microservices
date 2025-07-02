package com.cinema.movies.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author jaouad err
 * @since 15.09.24
 */
public class ObjectValidationException extends BaseException {

    @Getter
    private final Set<String> violations;

    @Getter
    private final String violationSource;

    // Constructeur avec message par défaut
    public ObjectValidationException(Set<String> violations, String violationSource) {
        super("Objet valide mais avec des violations");  // Message par défaut
        this.violations = violations;
        this.violationSource = violationSource;
    }

    // Constructeur avec message explicite
    public ObjectValidationException(String message, Set<String> violations, String violationSource) {
        super(message);
        this.violations = violations;
        this.violationSource = violationSource;
    }
}

