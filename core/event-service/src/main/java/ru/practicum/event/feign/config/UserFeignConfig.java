package ru.practicum.event.feign.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.event.feign.decoder.UserErrorDecoder;

@Configuration
public class UserFeignConfig {
    @Bean
    public ErrorDecoder userFeignErrorDecoder() {
        return new UserErrorDecoder();
    }
}
