package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.AdminEventApi;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.event.service.AdminEventService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminEventController implements AdminEventApi {
    private final AdminEventService eventService;

    @Override
    public List<EventFullDto> findAll(AdminEventParam params) {
        log.info("Admin: Method launched (findAllAdmin({}))", params);
        return eventService.findAllAdmin(params);
    }

    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest event) {
        log.info("Admin: Method launched (updateAdminEvent(Long eventId = {}, UpdateEventAdminRequest event = {}))", eventId, event);
        return eventService.updateAdminEvent(eventId, event);
    }
}
