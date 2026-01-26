package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.event.feign.client.RequestClient;
import ru.practicum.event.feign.client.UserClient;

@EnableFeignClients(clients = {
        RequestClient.class,
        UserClient.class
})
@SpringBootApplication
public class EventApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }
}
