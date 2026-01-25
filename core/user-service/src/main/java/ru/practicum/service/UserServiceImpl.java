package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.model.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto save(NewUserRequest user) {
        User savedUser = userRepository.save(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public List<UserDto> findAll(UserParam userParam) {
        Iterable<User> users = userParam.ids() != null && !userParam.ids().isEmpty() ?
                userRepository.findAll(UserRepository.Predicate.byIds(userParam.ids())) : userRepository.findAll();

        return StreamSupport.stream(users.spliterator(), false)
                .sorted(Comparator.comparing(User::getId))
                .skip(userParam.from())
                .limit(userParam.size())
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет такого пользователя id = " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void checkIfExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
    }

    @Override
    public UserShortDto findShortById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id = " + userId + " не найден")
                );

        return UserMapper.mapToUserShortDto(user);
    }

    @Override
    public Map<Long, UserShortDto> findShortByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        Iterable<User> users = userRepository.findAllById(userIds);

        return StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toMap(
                        User::getId,
                        UserMapper::mapToUserShortDto
                ));
    }
}