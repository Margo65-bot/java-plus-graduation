package ru.practicum.dto.event;

public record EventAvailabilityDto(
        Long eventId,
        Long confirmedRequests,
        Long participantLimit,
        Boolean available
) {
}
