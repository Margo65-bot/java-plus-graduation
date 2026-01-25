package ru.practicum.api.event;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.dto.event.EventInternalDto;

@RequestMapping("/internal/events")
public interface InternalEventApi {
    @GetMapping("/{eventId}")
    EventInternalDto findById(@PathVariable Long eventId);

    @GetMapping("/{eventId}/validate")
    void validateEvent(@PathVariable Long eventId);
}
