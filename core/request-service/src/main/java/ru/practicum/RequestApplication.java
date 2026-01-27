package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.feign.client.EventClient;
import ru.practicum.feign.client.UserClient;

@EnableFeignClients(clients = {
        EventClient.class,
        UserClient.class
})
@SpringBootApplication
public class RequestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestApplication.class, args);
    }
}
