package ru.practicum.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record LocationDto(
        @NotNull Float lat,
        @NotNull Float lon
) {
    @Builder
    public LocationDto {}
}
