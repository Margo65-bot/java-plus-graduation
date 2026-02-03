package ru.practicum.event.model.mapper;

import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestInternalDto;

import java.util.List;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto fromInternal(
            RequestInternalDto dto,
            Long eventId
    ) {
        return ParticipationRequestDto.builder()
                .id(dto.id())
                .status(dto.status())
                .created(dto.created())
                .requesterId(dto.requesterId())
                .eventId(eventId)
                .build();
    }

    public static List<ParticipationRequestDto> fromInternal(
            List<RequestInternalDto> dtos,
            Long eventId
    ) {
        return dtos.stream()
                .map(dto -> fromInternal(dto, eventId))
                .toList();
    }
}
