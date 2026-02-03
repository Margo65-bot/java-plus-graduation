package ru.practicum.compilation.model;

import ru.practicum.event.model.Event;

public record EventCompilationId(Long compilationId, Event event) {
}
