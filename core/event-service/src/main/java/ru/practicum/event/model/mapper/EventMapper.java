package ru.practicum.event.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventInternalDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.State;
import ru.practicum.event.model.Event;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class,
                LocationMapper.class
        }
)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "state", source = "state")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    Event toEntity(NewEventDto dto, Category category, State state);

    default Event toEntity(NewEventDto dto, Category category) {
        return toEntity(dto, category, State.PENDING);
    }

    @Mapping(target = "initiatorId", ignore = true)
    EventFullDto toFullDto(Event event);

    @Mapping(target = "initiatorId", ignore = true)
    EventShortDto toShortDto(Event event);

    default EventInternalDto toInternalDto(Event event) {
        return new EventInternalDto(
                event.getId(),
                event.getInitiatorId(),
                event.getCategory().getId(),
                event.getState(),
                event.getRequestModeration(),
                event.getParticipantLimit()
        );
    }
}