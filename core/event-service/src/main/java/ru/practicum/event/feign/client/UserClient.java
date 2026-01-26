package ru.practicum.event.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.InternalUserApi;
import ru.practicum.event.feign.config.UserFeignConfig;

@FeignClient(
        name = "user-service",
        configuration = UserFeignConfig.class,
        path = "/internal/users"
)
public interface UserClient extends InternalUserApi {
}
