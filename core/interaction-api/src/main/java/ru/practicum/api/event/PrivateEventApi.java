package ru.practicum.api.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPrivateParam;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

@Validated
@RequestMapping("/users/{userId}/events")
public interface PrivateEventApi {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto createEvent(
            @PathVariable @Positive(message = "userId должен быть больше 0") Long userId,
            @Valid @RequestBody NewEventDto dto
    );

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> findUserEvents(
            @PathVariable @Positive(message = "userId должен быть больше 0") Long userId,
            @Valid @ModelAttribute EventPrivateParam params
    );

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto findUserEventById(
            @PathVariable @Positive(message = "userId должен быть больше 0") Long userId,
            @PathVariable @Positive(message = "eventId должен быть больше 0") Long eventId
    );

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto updateUserEvent(
            @PathVariable @Positive(message = "userId должен быть больше 0") Long userId,
            @PathVariable @Positive(message = "eventId должен быть больше 0") Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest
    );

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    List<ParticipationRequestDto> findEventRequests(
            @PathVariable @Positive(message = "userId должен быть больше 0") Long userId,
            @PathVariable @Positive(message = "eventId должен быть больше 0") Long eventId
    );

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable @Positive(message = "userId должен быть больше 0") Long userId,
            @PathVariable @Positive(message = "eventId должен быть больше 0") Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest
    );
}
