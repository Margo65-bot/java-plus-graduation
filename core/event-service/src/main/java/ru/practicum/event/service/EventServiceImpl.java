package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventInternalDto;
import ru.practicum.dto.event.EventPrivateParam;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateRequestParam;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UpdateEventUserRequestParam;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestInternalDto;
import ru.practicum.dto.request.RequestStatusUpdateCommand;
import ru.practicum.event.feign.client.RequestClient;
import ru.practicum.event.feign.client.UserClient;
import ru.practicum.event.model.Event;
import ru.practicum.dto.event.EventSort;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.model.mapper.LocationMapper;
import ru.practicum.event.model.mapper.ParticipationRequestMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.event.util.OffsetBasedPageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final long MIN_HOURS_BEFORE_PUBLICATION_FOR_ADMIN = 1;
    private static final long MIN_HOURS_BEFORE_UPDATE_FOR_USER = 2;
    private static final LocalDateTime START_DATE_FOR_STAT_REQUEST = LocalDateTime.now().minusYears(1);

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final RequestClient requestClient;
    private final UserClient userClient;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;

    // ADMIN METHODS

    @Override
    public List<EventFullDto> findAllAdmin(AdminEventParam params) {
        int from = params.from();
        int size = params.size();
        Pageable pageable = new OffsetBasedPageable(from, size);

        List<Event> events = eventRepository
                .findAll(EventRepository.Predicate.adminFilters(params), pageable).getContent();

        setViewsAndConfirmedRequests(events);

        return events.stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(long id, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id " + id + " не найдена"));

        updateEvent(event, updateRequest);

        setViewsAndConfirmedRequests(event);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    //PUBLIC METHODS

    @Override
    public List<EventShortDto> findPublicEvents(EventPublicParam params) {
        int from = params.from();
        int size = params.size();

        Sort defaultSort = Sort.by("eventDate");
        Pageable pageable = new OffsetBasedPageable(from, size, defaultSort);

        BooleanBuilder predicate = EventRepository.Predicate.publicFilters(params);

        List<Event> events = eventRepository
                .findAll(predicate, pageable)
                .getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        if (params.onlyAvailable() == null || !params.onlyAvailable()) {
            setViewsAndConfirmedRequests(events);

            Comparator<EventShortDto> comparator =
                    createEventShortDtoComparator(params.sort());

            return events.stream()
                    .map(eventMapper::toShortDto)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> confirmedCounts =
                requestClient.findConfirmedRequestsCountForList(eventIds);

        List<Event> availableEvents = events.stream()
                .filter(event -> {
                    if (event.getParticipantLimit() == 0) {
                        return true;
                    }
                    long confirmed =
                            confirmedCounts.getOrDefault(event.getId(), 0L);
                    return confirmed < event.getParticipantLimit();
                })
                .toList();

        if (availableEvents.isEmpty()) {
            return Collections.emptyList();
        }

        setViewsAndConfirmedRequests(availableEvents);

        Comparator<EventShortDto> comparator =
                createEventShortDtoComparator(params.sort());

        return availableEvents.stream()
                .map(eventMapper::toShortDto)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findPublicEventById(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Event with id %d not found", eventId))
                );
        setViewsAndConfirmedRequests(event);
        return eventMapper.toFullDto(event);
    }

    // PRIVATE METHODS

    @Override
    public List<EventShortDto> findUserEvents(Long userId, EventPrivateParam params) {
        int from = params.from();
        int size = params.size();
        Sort defaultSort = Sort.by("id").descending();

        userClient.validateUser(userId);

        Pageable pageable = new OffsetBasedPageable(from, size, defaultSort);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        setViewsAndConfirmedRequests(events);

        return events.stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        userClient.validateUser(userId);

        Category category = categoryRepository.findById(dto.category()).orElseThrow(
                () -> new NotFoundException(String.format("Category with id %d not found", dto.category())));

        Event event = eventMapper.toEntity(dto, category);
        event.setInitiatorId(userId);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toFullDto(savedEvent);
    }

    @Override
    public EventFullDto findUserEventById(Long eventId, Long userId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id %d by user %d not found", eventId, userId))
        );
        setViewsAndConfirmedRequests(event);
        return eventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(UpdateEventUserRequestParam requestParam) {
        Event event = eventRepository.findByIdAndInitiatorId(requestParam.eventId(), requestParam.userId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "Event with id %d by user %d not found",
                                        requestParam.eventId(),
                                        requestParam.userId()
                                )
                        )
                );

        UpdateEventUserRequest updateRequest = requestParam.request();

        updateEvent(event, updateRequest);
        eventRepository.save(event);
        setViewsAndConfirmedRequests(event);

        return eventMapper.toFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long eventId, Long userId) {
        userClient.validateUser(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Event with id " + eventId + " not found")
                );

        if (!event.getInitiatorId().equals(userId)) {
            throw new AccessDeniedException("User is not initiator of event");
        }

        List<RequestInternalDto> requests = requestClient.findByEvent(eventId);

        return ParticipationRequestMapper.fromInternal(requests, eventId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequestParam requestParam) {
        EventRequestStatusUpdateRequest updateRequest = requestParam.updateRequest();
        Event event = eventRepository.findByIdAndInitiatorId(requestParam.eventId(), requestParam.userId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "Event with id %d by user %d not found",
                                        requestParam.eventId(),
                                        requestParam.userId()
                                )
                        )
                );

        event.setConfirmedRequests(requestClient.findConfirmedRequestsCountForOne(event.getId()));

        if (event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConditionsNotMetException("Достигнут лимит по заявкам на событие - " + event.getId());
        }

        RequestStatusUpdateCommand command = new RequestStatusUpdateCommand(
                event.getId(),
                updateRequest.requestIds(),
                updateRequest.status(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getConfirmedRequests()
        );

        return requestClient.updateStatuses(command);
    }

    // INTERNAL METHODS

    @Override
    public EventInternalDto findByIdInternal(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Event with id " + eventId + " not found")
                );
        return eventMapper.toInternalDto(event);
    }

    @Override
    public void checkIfExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id " + eventId + " не найдено");
        }
    }

    // HELPER METHODS

    private Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<ResponseStatsDto> stats = statsClient.get(createRequestStatsDto(uris, true));

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> extractEventIdFromUri(stat.uri()),
                        ResponseStatsDto::hits,
                        (existing, replacement) -> existing
                ));
    }

    private Long extractEventIdFromUri(String uri) {
        try {
            return Long.parseLong(uri.replace("/events/", ""));
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private Long getViews(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        Long views = 0L;
        try {
            views = statsClient.get(createRequestStatsDto(uris, true))
                    .getFirst()
                    .hits();
        } catch (Exception e) {
            return views;
        }
        return views;
    }

    private RequestStatsDto createRequestStatsDto(List<String> uris, boolean unique) {
        return new RequestStatsDto(
                START_DATE_FOR_STAT_REQUEST,
                LocalDateTime.now(),
                uris,
                unique
        );
    }

    private void setViewsAndConfirmedRequests(Event event) {
        setViews(event);
        event.setConfirmedRequests(requestClient.findConfirmedRequestsCountForOne(event.getId()));
    }

    private void setViewsAndConfirmedRequests(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> viewsMap = getViewsForEvents(eventIds);
        Map<Long, Long> confirmedRequestsMap = requestClient.findConfirmedRequestsCountForList(eventIds);

        events.forEach(event -> {
            event.setViews(viewsMap.getOrDefault(event.getId(), 0L));
            event.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L));
        });
    }

    private void setViews(Event event) {
        event.setViews(getViews(event.getId()));
    }

    private void updateEvent(Event event, UpdateEventUserRequest updateRequest) {
        LocalDateTime now = LocalDateTime.now();

        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CANCELED)) {
            throw new ConditionsNotMetException("Ожидается статус PENDING или CANCELED, получен - " + event.getState());
        }

        if (now.plusHours(MIN_HOURS_BEFORE_UPDATE_FOR_USER).isAfter(event.getEventDate())) {
            throw new ConditionsNotMetException("Изменить можно события запланированные " +
                    "на время не ранее чем через 2 часа от текущего, разница времени - " +
                    Duration.between(now, event.getEventDate()).toHours());
        }

        Optional.ofNullable(updateRequest.annotation())
                .filter(ann -> !ann.isBlank()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateRequest.description())
                .filter(desc -> !desc.isBlank()).ifPresent(event::setDescription);
        Optional.ofNullable(updateRequest.eventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateRequest.location())
                .map(locationMapper::toEntity)
                .ifPresent(event::setLocation);
        Optional.ofNullable(updateRequest.paid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateRequest.participantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateRequest.requestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateRequest.title()).filter(title -> !title.isBlank()).ifPresent(event::setTitle);

        Optional.ofNullable(updateRequest.category()).ifPresent(
                categoryId -> event.setCategory(categoryRepository.findById(categoryId)
                        .orElseThrow(
                                () -> new NotFoundException("Category id " + categoryId + " not found")
                        ))
        );

        if (updateRequest.stateAction() != null) {
            switch (updateRequest.stateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
            }
        }
    }

    private void updateEvent(Event event, UpdateEventAdminRequest updateRequest) {
        LocalDateTime now = LocalDateTime.now();

        if (event.getState() != State.PENDING) {
            throw new ConditionsNotMetException(
                    "Событие можно публиковать или отклонить, только если оно в состоянии ожидания публикации. Настоящее состояние: "
                            + event.getState());
        }

        if (now.plusHours(MIN_HOURS_BEFORE_PUBLICATION_FOR_ADMIN).isAfter(event.getEventDate())) {
            throw new ConditionsNotMetException(
                    "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации, разница времени - " +
                            Duration.between(now, event.getEventDate()).toHours());
        }

        Optional.ofNullable(updateRequest.annotation())
                .filter(ann -> !ann.isBlank()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateRequest.description())
                .filter(desc -> !desc.isBlank()).ifPresent(event::setDescription);
        Optional.ofNullable(updateRequest.eventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateRequest.location())
                .map(locationMapper::toEntity)
                .ifPresent(event::setLocation);
        Optional.ofNullable(updateRequest.paid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateRequest.participantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateRequest.requestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateRequest.title()).filter(title -> !title.isBlank()).ifPresent(event::setTitle);

        Optional.ofNullable(updateRequest.category()).ifPresent(
                categoryId -> event.setCategory(categoryRepository.findById(categoryId)
                        .orElseThrow(
                                () -> new NotFoundException("Category id " + categoryId + " not found")
                        ))
        );

        if (updateRequest.stateAction() != null) {
            switch (updateRequest.stateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(State.CANCELED);
            }
        }
    }

    private Comparator<EventShortDto> createEventShortDtoComparator(EventSort sort) {
        Comparator<EventShortDto> comparator;

        if (sort == null) {
            comparator = (a, b) -> 0;
        } else {
            comparator = switch (sort) {
                case VIEWS -> Comparator.comparing(EventShortDto::views).reversed();
                case EVENT_DATE -> Comparator.comparing(EventShortDto::eventDate);
            };
        }
        return comparator;
    }
}
