package com.elhg.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfiguration {
    @Value("http://localhost:8080/")
    private String baseUrl;

    @Bean
    public WebClient.Builder webClientBuilder() {
        log.info("Creating WebClient with base URL: {}", baseUrl);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((name, values) -> {
                        for (String value : values) {
                            log.info("Header Request: {}: {}", name, value);
                        }
                    });
                    return Mono.just(clientRequest);
                })).filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    log.info("Response Status: {}", clientResponse.statusCode());
                    log.info("Response Client : {}", clientResponse);
                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> {
                        for (String value : values) {
                            log.info("Header Response: {}: {}", name, value);
                        }
                    });
                    return Mono.just(clientResponse);
                }));



    }
}
