package ru.practicum.dto.request;

import java.time.LocalDateTime;

public record RequestInternalDto(
        Long id,
        Long requesterId,
        RequestStatus status,
        LocalDateTime created
) {
}
