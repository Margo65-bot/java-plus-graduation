package ru.practicum.api.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Validated
@RequestMapping("/events")
public interface PublicEventApi {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> findPublicEvents(
            @Valid @ModelAttribute EventPublicParam params,
            HttpServletRequest request);

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto findPublicEventById(
            @Positive(message = "eventId должен быть больше 0") @PathVariable Long eventId,
            HttpServletRequest request
    );
}
