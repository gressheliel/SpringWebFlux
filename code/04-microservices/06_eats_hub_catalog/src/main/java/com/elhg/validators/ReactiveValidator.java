package com.elhg.validators;

import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReactiveValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T object) {
        final var violations = validator.validate(object);
        if (violations.isEmpty()) {
            return Mono.just(object);
        }

        final var errors = violations.stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(" , "));

        return Mono.error(new ValidationException(errors));
    }

}
