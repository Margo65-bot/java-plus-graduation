package ru.practicum.api.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.util.List;

@Validated
@RequestMapping("/admin/events")
public interface AdminEventApi {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventFullDto> findAll(
            @Valid @ModelAttribute AdminEventParam params
    );

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto update(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest event
    );
}
