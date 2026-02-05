package ru.practicum.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.data.entity.UserAction;
import ru.practicum.data.entity.mapper.UserActionMapper;
import ru.practicum.data.repository.UserActionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;
import java.util.Set;

import static ru.practicum.util.ActionWeights.WEIGHTS;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepository userActionRepository;

    @Override
    @Transactional
    public void save(UserActionAvro userActionAvro) {
        UserAction action = userActionRepository.findByUserIdAndEventId(
                userActionAvro.getUserId(),
                userActionAvro.getEventId()
        ).orElseGet(() ->
                userActionRepository.save(UserActionMapper.mapToModel(userActionAvro))
        );

        if (WEIGHTS.get(userActionAvro.getActionType()) > WEIGHTS.get(action.getActionType())) {
            action.setActionType(userActionAvro.getActionType());
        }
    }

    @Override
    public List<Long> findByUserId(Long userId, Pageable pageable) {
        return userActionRepository.findByUserId(userId, pageable);
    }

    @Override
    public Set<Long> findByUserIdExcludeEventId(Long userId, Long eventId) {
        return userActionRepository.findByUserIdExcludeEventId(userId, eventId);
    }

    @Override
    public List<UserAction> findByEventIdIn(Set<Long> eventIds) {
        return userActionRepository.findByEventIdIn(eventIds);
    }
}
