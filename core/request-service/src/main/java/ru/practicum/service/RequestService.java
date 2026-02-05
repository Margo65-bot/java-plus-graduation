package ru.practicum.service;

import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestInternalDto;
import ru.practicum.dto.request.RequestStatusUpdateCommand;

import java.util.List;
import java.util.Map;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto createRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    Map<Long, Long> findConfirmedRequestsCountForList(List<Long> eventIds);

    Long findConfirmedRequestsCountForOne(Long eventId);

    List<RequestInternalDto> findByEvent(Long eventId);

    EventRequestStatusUpdateResult updateStatuses(RequestStatusUpdateCommand command);

    void validateParticipant(Long eventId, Long userId);
}
