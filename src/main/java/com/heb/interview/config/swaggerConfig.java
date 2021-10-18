package com.heb.interview.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class swaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Contact Application API").description(
                        "This is a spring Boot RESTful service that ingests user images," +
                                "analyzes them for object detection, and returns the " +
                                "enhanced content using springdoc and OpenAPI for HEB interview")
                        .version("1.0"));
    }

}
