package org.auth.api.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.auth.api.infrastructure.config.json.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return Json.mapper();
    }
}
