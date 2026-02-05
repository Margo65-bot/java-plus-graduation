package ru.practicum.data.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.data.entity.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;
import java.util.Set;

public interface UserActionService {
    void save(UserActionAvro userActionAvro);

    List<Long> findByUserId(Long userId, Pageable pageable);

    Set<Long> findByUserIdExcludeEventId(Long userId, Long eventId);

    List<UserAction> findByEventIdIn(Set<Long> eventIds);
}
