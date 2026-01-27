package ru.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.InternalUserApi;
import ru.practicum.feign.config.UserFeignConfig;
import ru.practicum.feign.fallback.UserClientFallback;

@FeignClient(
        name = "user-service",
        configuration = UserFeignConfig.class,
        path = "/internal/users",
        fallback = UserClientFallback.class
)
public interface UserClient extends InternalUserApi {
}
