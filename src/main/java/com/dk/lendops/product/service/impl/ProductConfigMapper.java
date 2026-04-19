package com.dk.lendops.product.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Maps config objects to JSON
 *
 * @author David Kariuki
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductConfigMapper {

    private final ObjectMapper objectMapper;

    /**
     * Converts config payload to JSON
     *
     * @param config Config payload
     * @return JSON string
     */
    public String toJson(Object config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Invalid config payload", e);
        }
    }

    /**
     * Converts JSON string to target type
     *
     * @param json        JSON string
     * @param targetClass Target class
     * @param <T>         Type
     * @return Typed config
     */
    public <T> T fromJson(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Invalid stored config for " + targetClass.getSimpleName());
        }
    }

    /**
     * Converts config payload to target type
     *
     * @param config      Config payload
     * @param targetClass Target class
     * @param <T>         Type
     * @return Typed config
     */
    public <T> T convertValue(Object config, Class<T> targetClass) {
        try {
            return objectMapper.convertValue(config, targetClass);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Invalid config payload for ".concat(targetClass.getSimpleName()));
        }
    }
}
