package ru.practicum.dto.event;

public record EventInternalDto(
        Long id,
        Long initiatorId,
        Long categoryId,
        State state,
        Boolean requestModeration,
        Integer participantLimit
) {
}
