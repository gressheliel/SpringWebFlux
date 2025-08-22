package com.elhg.handlers;

import com.elhg.constants.ErrorConstants;
import com.elhg.exceptions.BusinessException;
import com.elhg.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)  // Ensure this handler is called first
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Global error handler caught an exception: {}", ex.getMessage(), ex);

        // Estructura de la respuesta de error
        HttpStatus status;
        String message;
        String errorType;

        if (ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
            errorType = ErrorConstants.ERROR_TYPE_VALIDATION;
        } else if (ex instanceof BusinessException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            message = ex.getMessage();
            errorType = ErrorConstants.ERROR_TYPE_BUSINESS;
        } else if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = ex.getMessage();
            errorType = ErrorConstants.ERROR_TYPE_BUSINESS;
        } else if (Objects.nonNull(ex.getMessage()) && ex.getMessage().contains(ErrorConstants.ERROR_MESSAGE_RESTAURANT_NOT_FOUND)) {
            status = HttpStatus.NOT_FOUND;
            message = ErrorConstants.ERROR_MESSAGE_RESTAURANT_NOT_FOUND;
            errorType = ErrorConstants.ERROR_TYPE_NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = ErrorConstants.ERROR_MESSAGE_UNEXPECTED;
            errorType = ErrorConstants.ERROR_TYPE_INTERNAL;
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(ErrorConstants.RESPONSE_KEY_TIMESTAMP, LocalDateTime.now().toString());
        errorResponse.put(ErrorConstants.RESPONSE_KEY_PATH, exchange.getRequest().getPath().value());
        errorResponse.put(ErrorConstants.RESPONSE_KEY_STATUS, status.value());
        errorResponse.put(ErrorConstants.RESPONSE_KEY_ERROR, status.getReasonPhrase());
        errorResponse.put(ErrorConstants.RESPONSE_KEY_MESSAGE, message);
        errorResponse.put(ErrorConstants.RESPONSE_KEY_ERROR_TYPE, errorType);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        try {
            final var bytes = objectMapper.writeValueAsBytes(errorResponse);
            final var dataBuffer = Mono.just(exchange.getResponse().bufferFactory().wrap(bytes));
            return exchange.getResponse().writeWith(dataBuffer);
        }catch (Exception e) {
            log.error("Error while writing error response: {}", e.getMessage());
            return Mono.error(e);
        }

    }
}
