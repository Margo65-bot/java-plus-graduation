package ru.practicum.api.request;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

@Validated
@RequestMapping("/users/{userId}/requests")
public interface PrivateRequestApi {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto createRequest(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "eventId") long eventId
    );

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    ParticipationRequestDto cancelRequest(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "requestId") long requestId
    );

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ParticipationRequestDto> getUserRequests(@PathVariable(name = "userId") long userId);
}
