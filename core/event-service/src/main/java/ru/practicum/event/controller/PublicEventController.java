package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.PublicEventApi;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.event.service.PublicEventService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicEventController implements PublicEventApi {
    private final PublicEventService eventService;

    @Override
    public List<EventShortDto> findPublicEvents(EventPublicParam params, HttpServletRequest request) {
        log.info("Public: Method launched (findPublicEvents({}))", params);
        return eventService.findPublicEvents(params);
    }

    @Override
    public EventFullDto findPublicEventById(Long eventId, Long userId) {
        log.info("Public: Method launched (findPublicEventById(Long eventId={}, Long userId={}))", eventId, userId);
        return eventService.findPublicEventById(eventId, userId);
    }

    @Override
    public List<EventShortDto> getRecommendationsForUser(Long userId) {
        log.info("Public: Method launched (getRecommendationsForUser(Long userId={}))", userId);
        return eventService.getRecommendationsForUser(userId);
    }

    @Override
    public void addLike(Long eventId, Long userId) {
        log.info("Public: Method launched (addLike(Long eventId={}, Long userId={}))", eventId, userId);
        eventService.addLike(eventId, userId);
    }
}