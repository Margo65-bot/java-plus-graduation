package ru.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.event.InternalEventApi;

@FeignClient(name = "event-service")
public interface EventClient extends InternalEventApi {
}
