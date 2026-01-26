package ru.practicum.feign.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.feign.decoder.EventErrorDecoder;

@Configuration
public class EventFeignConfig {
    @Bean
    public ErrorDecoder requestFeignErrorDecoder() {
        return new EventErrorDecoder();
    }
}
