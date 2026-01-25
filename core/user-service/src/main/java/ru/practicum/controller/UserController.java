package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.user.UserApi;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    public UserDto save(NewUserRequest user) {
        log.info("Method launched (save(UserCreateDto user = {}))", user);
        return userService.save(user);
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Method launched (deleteById(Long userId = {}))", userId);
        userService.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        log.info("Method launched (findAll(List<Long> ids = {}, Integer from = {}, Integer size = {}))", ids, from, size);
        UserParam userParam = new UserParam(ids, from, size);
        return userService.findAll(userParam);
    }
}