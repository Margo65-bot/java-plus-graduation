package ru.practicum.api.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;

public interface InternalUserApi {
    @GetMapping("/{userId}/validate")
    void validateUser(@PathVariable Long userId);

    @GetMapping("/{userId}")
    UserShortDto findById(@PathVariable Long userId);

    @PostMapping
    Map<Long, UserShortDto> findAllByIds(@RequestBody List<Long> userIds);
}
