package ru.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.event.InternalEventApi;
import ru.practicum.feign.config.EventFeignConfig;

@FeignClient(
        name = "event-service",
        configuration = EventFeignConfig.class,
        path = "/internal/events"
)
public interface EventClient extends InternalEventApi {
}
