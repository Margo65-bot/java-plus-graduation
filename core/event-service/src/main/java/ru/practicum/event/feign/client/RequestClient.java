package ru.practicum.event.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.request.InternalRequestApi;

@FeignClient(name = "request-service")
public interface RequestClient extends InternalRequestApi {
}
