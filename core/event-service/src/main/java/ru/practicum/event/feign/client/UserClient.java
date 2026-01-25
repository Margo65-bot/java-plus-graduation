package ru.practicum.event.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.InternalUserApi;

@FeignClient(name = "user-service")
public interface UserClient extends InternalUserApi {
}
