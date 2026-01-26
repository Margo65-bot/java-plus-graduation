package ru.practicum.feign.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.feign.decoder.UserErrorDecoder;

@Configuration
public class UserFeignConfig {
    @Bean
    public ErrorDecoder userFeignErrorDecoder() {
        return new UserErrorDecoder();
    }
}
