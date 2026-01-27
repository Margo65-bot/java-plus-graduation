package ru.practicum.dto.request;

import java.util.Set;

public record RequestStatusUpdateCommand(
        Long eventId,
        Set<Long> requestIds,
        RequestStatus status,
        Integer participantLimit,
        Boolean requestModeration,
        Long confirmedRequests
) {
}
