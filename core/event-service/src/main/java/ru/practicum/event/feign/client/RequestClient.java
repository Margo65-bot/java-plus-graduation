package ru.practicum.event.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.request.InternalRequestApi;
import ru.practicum.event.feign.config.RequestFeignConfig;
import ru.practicum.event.feign.fallback.RequestClientFallback;

@FeignClient(
        name = "request-service",
        configuration = RequestFeignConfig.class,
        path = "/internal/requests",
        fallback = RequestClientFallback.class
)
public interface RequestClient extends InternalRequestApi {
}
