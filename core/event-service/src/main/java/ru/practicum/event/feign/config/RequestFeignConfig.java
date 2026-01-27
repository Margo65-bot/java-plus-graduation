package ru.practicum.event.feign.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.event.feign.decoder.RequestErrorDecoder;

@Configuration
public class RequestFeignConfig {
    @Bean
    public ErrorDecoder requestFeignErrorDecoder() {
        return new RequestErrorDecoder();
    }
}
