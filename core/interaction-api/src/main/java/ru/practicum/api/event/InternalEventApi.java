package ru.practicum.api.event;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.dto.event.EventInternalDto;

public interface InternalEventApi {
    @GetMapping("/{eventId}")
    EventInternalDto findById(@PathVariable Long eventId);

    @PostMapping("/{eventId}/validate")
    void validateEvent(@PathVariable Long eventId);
}
