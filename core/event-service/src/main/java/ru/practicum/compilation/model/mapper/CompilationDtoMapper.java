package ru.practicum.compilation.model.mapper;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

public class CompilationDtoMapper {
    public static CompilationDto mapCompilationToDto(Compilation compilation, Set<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .eventIds(events)
                .build();
    }
}
