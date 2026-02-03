package ru.practicum.event.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.LocationDto;
import ru.practicum.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toDto(Location location);

    Location toEntity(LocationDto dto);
}

