package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto save(NewUserRequest user);

    List<UserDto> findAll(UserParam userParam);

    void deleteById(Long userId);

    void checkIfExists(Long userId);

    UserShortDto findShortById(Long userId);

    Map<Long, UserShortDto> findShortByIds(List<Long> userIds);
}