package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.PrivateEventApi;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPrivateParam;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateRequestParam;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UpdateEventUserRequestParam;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController implements PrivateEventApi {
    private final EventService eventService;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        log.info("Private: Method launched (createEvent({}, {}))", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    @Override
    public List<EventShortDto> findUserEvents(Long userId, EventPrivateParam params) {
        log.info("Private: Method launched (findUserEvents({}))", params);
        return eventService.findUserEvents(userId, params);
    }

    @Override
    public EventFullDto findUserEventById(Long userId, Long eventId) {
        log.info("Private: Method launched (findUserEventById({}, {}))", eventId, userId);
        return eventService.findUserEventById(eventId, userId);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        UpdateEventUserRequestParam updateEventUserRequestParam = new UpdateEventUserRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateUserEvent({}))", updateEventUserRequestParam);
        return eventService.updateUserEvent(updateEventUserRequestParam);
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId) {
        log.info("Private: Method launched (findEventRequests({}, {}))", eventId, userId);
        return eventService.findEventRequests(eventId, userId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateRequestParam updateEventRequestParam = new EventRequestStatusUpdateRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateRequestStatus({}))", updateEventRequestParam);
        return eventService.updateRequestStatus(updateEventRequestParam);
    }
}
