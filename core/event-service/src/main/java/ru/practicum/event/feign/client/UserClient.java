package ru.practicum.event.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.InternalUserApi;
import ru.practicum.event.feign.config.UserFeignConfig;
import ru.practicum.event.feign.fallback.UserClientFallback;

@FeignClient(
        name = "user-service",
        configuration = UserFeignConfig.class,
        path = "/internal/users",
        fallback = UserClientFallback.class
)
public interface UserClient extends InternalUserApi {
}
