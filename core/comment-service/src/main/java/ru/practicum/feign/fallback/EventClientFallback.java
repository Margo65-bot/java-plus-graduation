package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.event.EventInternalDto;
import ru.practicum.dto.event.State;
import ru.practicum.feign.client.EventClient;

@Component
public class EventClientFallback implements EventClient {
    @Override
    public EventInternalDto findById(Long eventId) {
        return new EventInternalDto(
                eventId,
                0L,
                0L,
                State.PUBLISHED,
                false,
                0
        );
    }

    @Override
    public void validateEvent(Long eventId) {

    }
}
