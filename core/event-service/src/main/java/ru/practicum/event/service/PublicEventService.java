package ru.practicum.event.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

public interface PublicEventService {
    List<EventShortDto> findPublicEvents(EventPublicParam params);

    EventFullDto findPublicEventById(Long eventId);
}
