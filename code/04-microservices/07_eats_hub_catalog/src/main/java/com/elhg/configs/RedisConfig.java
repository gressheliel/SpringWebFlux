package com.elhg.configs;

import com.elhg.dtos.responses.RestaurantResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("localhost")
    private String redisHost;

    @Value("6379")
    private int redisPort;

    @Value("debuggeandoideas")
    private String redisPassword;

    @Value("0")
    private int redisDatabase;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(){
        log.info("Creating LettuceConnectionFactory with host: {}, port: {}, password: {}, database: {}",
                redisHost, redisPort, redisPassword, redisDatabase);

        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        redisStandaloneConfiguration.setPassword(redisPassword);
        redisStandaloneConfiguration.setDatabase(redisDatabase);

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(3))
                        .shutdownTimeout(Duration.ofMillis(150))
                .build();

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean
    public ReactiveRedisTemplate<String, RestaurantResponse> reactiveRedisTemplate(
            LettuceConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer<RestaurantResponse> serializer =
                new Jackson2JsonRedisSerializer<>(createObjectMapper(), RestaurantResponse.class);

        RedisSerializationContext<String, RestaurantResponse> context =
                RedisSerializationContext.<String, RestaurantResponse>newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(serializer)
                        .hashKey(StringRedisSerializer.UTF_8)
                        .hashValue(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, List<RestaurantResponse>> reactiveRedisListTemplate(
            LettuceConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = createObjectMapper();
        JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, RestaurantResponse.class);

        Jackson2JsonRedisSerializer<List<RestaurantResponse>> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, type);

        RedisSerializationContext<String, List<RestaurantResponse>> context =
                RedisSerializationContext.<String, List<RestaurantResponse>>newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(serializer)
                        .hashKey(StringRedisSerializer.UTF_8)
                        .hashValue(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void verifyRedisConnection() {
        log.info("Redis connection Successfully established...");
    }
}