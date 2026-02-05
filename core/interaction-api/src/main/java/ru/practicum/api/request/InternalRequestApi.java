package ru.practicum.api.request;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestInternalDto;
import ru.practicum.dto.request.RequestStatusUpdateCommand;

import java.util.List;
import java.util.Map;

public interface InternalRequestApi {
    @PostMapping("/confirmed/count")
    Map<Long, Long> findConfirmedRequestsCountForList(@RequestBody List<Long> eventIds);

    @GetMapping("/confirmed/count/{eventId}")
    Long findConfirmedRequestsCountForOne(@PathVariable Long eventId);

    @GetMapping("/event/{eventId}")
    List<RequestInternalDto> findByEvent(@PathVariable Long eventId);

    @PostMapping("/status")
    EventRequestStatusUpdateResult updateStatuses(
            @RequestBody RequestStatusUpdateCommand command
    );

    @PostMapping("/event/{eventId}/participants/{userId}")
    void validateParticipant(
            @PathVariable Long eventId,
            @PathVariable Long userId
    );
}
