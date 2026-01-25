package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.InternalEventApi;
import ru.practicum.dto.event.EventInternalDto;
import ru.practicum.event.service.EventService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class InternalEventController implements InternalEventApi {
    private final EventService eventService;

    @Override
    public EventInternalDto findById(Long eventId) {
        log.info("Internal: Method launched (findById(Long eventId={}))", eventId);
        return eventService.findByIdInternal(eventId);
    }

    @Override
    public void validateEvent(Long eventId) {
        log.info("Internal: Method launched (validateEvent(Long eventId={}))", eventId);
        eventService.checkIfExists(eventId);
    }
}
