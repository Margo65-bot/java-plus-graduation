package ru.practicum.data.entity.mapper;

import ru.practicum.data.entity.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionMapper {
    public static UserAction mapToModel(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .actionType(userActionAvro.getActionType())
                .timestamp(userActionAvro.getTimestamp())
                .build();
    }
}
