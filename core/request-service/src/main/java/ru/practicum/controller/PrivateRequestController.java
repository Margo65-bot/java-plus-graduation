package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.request.PrivateRequestApi;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController implements PrivateRequestApi {
    private final RequestService requestService;


    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        log.info("Private: creating request for userId={}, eventId={}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        log.info("Private: cancelling request for userId={}, requestId={}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        log.info("Private: getting requests for userId={}", userId);
        return requestService.getUserRequests(userId);
    }
}
