package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.PublicEventApi;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicEventController implements PublicEventApi {
    private final EventService eventService;
    private final StatsClient statsClient;

    @Value("${stats.service.name}")
    private String serviceName;

    @Override
    public List<EventShortDto> findPublicEvents(EventPublicParam params, HttpServletRequest request) {
        log.info("Public: Method launched (findPublicEvents({}))", params);
        List<EventShortDto> events = eventService.findPublicEvents(params);
        if (!events.isEmpty()) {
            saveHit(request);
        }
        return events;
    }

    @Override
    public EventFullDto findPublicEventById(Long id, HttpServletRequest request) {
        log.info("Public: Method launched (findPublicEventById({}))", id);
        EventFullDto event = eventService.findPublicEventById(id);
        saveHit(request);
        return event;
    }

    private void saveHit(HttpServletRequest request) {
        try {
            statsClient.hit(HitCreateDto.builder()
                    .app(serviceName)
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.warn("Failed to save statistics for URI: {}", request.getRequestURI(), e);
            // Не бросаем исключение дальше - статистика не должна ломать основной flow
        }
    }
}
