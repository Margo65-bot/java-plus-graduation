package ru.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.InternalUserApi;
import ru.practicum.feign.config.UserFeignConfig;

@FeignClient(
        name = "user-service",
        configuration = UserFeignConfig.class,
        path = "/internal/users"
)
public interface UserClient extends InternalUserApi {
}
