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

    // ЗАЯВКИ НА КОНКРЕТНОЕ СОБЫТИЕ

//    // Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
//    @PatchMapping("/users/{userId}/events/{eventId}/requests")
//    @ResponseStatus(HttpStatus.OK)
//    EventRequestStatusUpdateResultDto moderateRequest(
//            @PathVariable @Positive(message = "User Id not valid") Long userId,
//            @PathVariable @Positive(message = "Event Id not valid") Long eventId,
//            @RequestBody @Valid EventRequestStatusUpdateRequestDto updateRequestDto
//    );
//
//    // Получение информации о запросах на участие в событии текущего пользователя
//    @GetMapping("/users/{userId}/events/{eventId}/requests")
//    @ResponseStatus(HttpStatus.OK)
//    Collection<ParticipationRequestDto> getEventRequests(
//            @PathVariable @Positive(message = "User Id not valid") Long userId,
//            @PathVariable @Positive(message = "Event Id not valid") Long eventId
//    );
//
//    // INTERACTION API
//
//    // Запрос количества подтвержденных заявок по списку eventId
//    @PostMapping("/requests/confirmed")
//    @ResponseStatus(HttpStatus.OK)
//    Map<Long, Long> getConfirmedRequestsByEventIds(
//            @RequestBody Collection<Long> eventIds
//    );
//
//    // Проверка участия пользователя в конкретном событии перед лайком
//    @GetMapping("/users/{userId}/events/{eventId}/check/participation")
//    @ResponseStatus(HttpStatus.OK)
//    String checkParticipation(
//            @PathVariable @Positive(message = "User Id not valid") Long userId,
//            @PathVariable @Positive(message = "Event Id not valid") Long eventId
//    );
}
