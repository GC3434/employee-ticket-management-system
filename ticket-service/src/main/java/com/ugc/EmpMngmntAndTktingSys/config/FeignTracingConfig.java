package com.ugc.EmpMngmntAndTktingSys.config;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTracingConfig {

    @Bean
    public MicrometerObservationCapability micrometerObservationCapability(
            ObservationRegistry observationRegistry) {

        return new MicrometerObservationCapability(observationRegistry);
    }
}