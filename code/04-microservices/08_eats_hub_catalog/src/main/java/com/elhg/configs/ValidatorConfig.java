package com.elhg.configs;


import jakarta.annotation.PreDestroy;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ValidatorConfig {

    private ValidatorFactory validatorFactory;

    @Bean
    public Validator validator(){
        validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.getValidator();
    }

    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(validatorFactory)) {
            validatorFactory.close();
        }
    }
}
