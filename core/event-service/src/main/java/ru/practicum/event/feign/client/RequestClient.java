package ru.practicum.event.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.request.InternalRequestApi;
import ru.practicum.event.feign.config.RequestFeignConfig;

@FeignClient(
        name = "request-service",
        configuration = RequestFeignConfig.class
)
public interface RequestClient extends InternalRequestApi {
}
