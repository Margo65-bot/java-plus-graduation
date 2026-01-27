package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.user.InternalUserApi;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class InternalUserController implements InternalUserApi {
    private final UserService userService;

    @Override
    public void validateUser(Long userId) {
        log.info("Method launched (validateUser(Long userId = {}))", userId);
        userService.checkIfExists(userId);
    }

    @Override
    public UserShortDto findById(Long userId) {
        log.info("Method launched (findShortById(Long userId = {}))", userId);
        return userService.findShortById(userId);
    }

    @Override
    public Map<Long, UserShortDto> findAllByIds(List<Long> userIds) {
        log.info("Method launched (findShortByIds(List<Long> userIds={}))", userIds);
        return userService.findShortByIds(userIds);
    }
}
