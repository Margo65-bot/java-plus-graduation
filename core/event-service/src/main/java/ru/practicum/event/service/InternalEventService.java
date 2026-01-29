package ru.practicum.event.service;

import ru.practicum.dto.event.EventInternalDto;

public interface InternalEventService {
    EventInternalDto findByIdInternal(Long eventId);

    void checkIfExists(Long eventId);
}
