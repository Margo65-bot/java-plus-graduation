package ru.practicum.event.service;

import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> findAllAdmin(AdminEventParam params);

    EventFullDto updateAdminEvent(long id, UpdateEventAdminRequest event);
}
