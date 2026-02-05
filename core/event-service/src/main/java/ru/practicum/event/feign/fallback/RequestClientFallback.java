package ru.practicum.event.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestInternalDto;
import ru.practicum.dto.request.RequestStatusUpdateCommand;
import ru.practicum.event.feign.client.RequestClient;

import java.util.List;
import java.util.Map;

@Component
public class RequestClientFallback implements RequestClient {

    @Override
    public Map<Long, Long> findConfirmedRequestsCountForList(List<Long> eventIds) {
        return Map.of();
    }

    @Override
    public Long findConfirmedRequestsCountForOne(Long eventId) {
        return 0L;
    }

    @Override
    public List<RequestInternalDto> findByEvent(Long eventId) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateStatuses(RequestStatusUpdateCommand command) {
        return new EventRequestStatusUpdateResult(
                List.of(),
                List.of()
        );
    }

    @Override
    public void validateParticipant(Long eventId, Long userId) {

    }
}

