package ru.practicum.compilation.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.request.InternalRequestApi;

@FeignClient(name = "request-service")
public interface RequestClient extends InternalRequestApi {
}
