package com.elhg.controllers;

import com.elhg.dtos.RatingRequest;
import com.elhg.exceptions.StreamTerminateException;
import com.elhg.services.StreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "rating")
public class RatingController {

    private final StreamService streamService;

    @PostMapping
    public Mono<ResponseEntity<Object>> rating(@Valid @RequestBody RatingRequest ratingRequest) {
        log.info("Controller Received rating request: {}", ratingRequest);
        return this.streamService.sendRatingToStream(ratingRequest)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED)))
                .onErrorResume(ex -> {
                    if (ex instanceof StreamTerminateException) {
                        return Mono.just(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
                    }
                    return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }
}