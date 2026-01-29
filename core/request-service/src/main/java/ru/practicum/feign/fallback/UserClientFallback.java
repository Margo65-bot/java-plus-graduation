package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.feign.client.UserClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public void validateUser(Long userId) {
        // ничего не делаем — считаем пользователя валидным
    }

    @Override
    public UserShortDto findById(Long userId) {
        return new UserShortDto(userId, "unknown-user");
    }

    @Override
    public Map<Long, UserShortDto> findAllByIds(List<Long> userIds) {
        return userIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> new UserShortDto(id, "unknown-user")
                ));
    }
}
