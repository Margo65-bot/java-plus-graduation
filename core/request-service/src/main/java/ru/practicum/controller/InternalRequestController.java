package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.request.InternalRequestApi;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestInternalDto;
import ru.practicum.dto.request.RequestStatusUpdateCommand;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/internal/requests")
public class InternalRequestController implements InternalRequestApi {
    private final RequestService requestService;

    @Override
    public Map<Long, Long> findConfirmedRequestsCountForList(@RequestBody List<Long> eventIds) {
        log.info("Internal: findConfirmedRequestsCountForList(List<Long> eventIds={})", eventIds);
        return requestService.findConfirmedRequestsCountForList(eventIds);
    }

    @Override
    public Long findConfirmedRequestsCountForOne(Long eventId) {
        log.info("Internal: findConfirmedRequestsCountForOne(Long eventId={})", eventId);
        return requestService.findConfirmedRequestsCountForOne(eventId);
    }

    @Override
    public List<RequestInternalDto> findByEvent(Long eventId) {
        log.info("Internal: findByEvent(Long eventId={})", eventId);
        return requestService.findByEvent(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatuses(RequestStatusUpdateCommand command) {
        log.info("Internal: updateStatuses(RequestStatusUpdateCommand command={})", command);
        return requestService.updateStatuses(command);
    }

    @Override
    public void validateParticipant(Long eventId, Long userId) {
        log.info("validateParticipant(Long eventId={}, Long userId={})", eventId, userId);
        requestService.validateParticipant(eventId, userId);
    }
}
