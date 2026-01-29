package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventInternalDto;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.State;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestInternalDto;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.dto.request.RequestStatusUpdateCommand;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.feign.client.EventClient;
import ru.practicum.feign.client.UserClient;
import ru.practicum.model.Request;
import ru.practicum.model.mapper.RequestDtoMapper;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        userClient.validateUser(userId);
        return requestRepository.findUserRequests(userId).stream()
                .map(RequestDtoMapper::mapRequestToDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        userClient.validateUser(userId);

        EventInternalDto event = eventClient.findById(eventId);

        validateCreation(userId, event);

        RequestStatus status =
                event.requestModeration() && event.participantLimit() > 0
                        ? RequestStatus.PENDING
                        : RequestStatus.CONFIRMED;

        Request request = requestRepository.save(
                Request.builder()
                        .createdOn(LocalDateTime.now())
                        .eventId(eventId)
                        .requesterId(userId)
                        .status(status)
                        .build()
        );

        return RequestDtoMapper.mapRequestToDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        userClient.validateUser(userId);
        Request request = getRequestById(requestId);

        if (request.getRequesterId() != userId) {
            throw new ValidationException("Пользователь id=" + userId + " не может отменить заявку id=" + request.getId());
        }

        if (RequestStatus.CANCELED.equals(request.getStatus()) || RequestStatus.REJECTED.equals(request.getStatus())) {
            throw new ValidationException("Статус заявки " + request.getStatus() + " не позволяет выполнить отмену");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestDtoMapper.mapRequestToDto(requestRepository.save(request));
    }

    @Override
    public Map<Long, Long> findConfirmedRequestsCountForList(List<Long> eventIds) {
        try {
            return requestRepository.countConfirmedRequestsByEventIds(eventIds).stream()
                    .collect(Collectors.toMap(
                            e -> (Long) e[0],
                            e -> (Long) e[1]
                    ));
        } catch (Exception e) {
            return eventIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
        }
    }

    @Override
    public Long findConfirmedRequestsCountForOne(Long eventId) {
        if (eventId == null) {
            return 0L;
        }
        return requestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Override
    public List<RequestInternalDto> findByEvent(Long eventId) {
        return requestRepository.findByEventId(eventId).stream()
                .map(RequestDtoMapper::mapToInternalDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatuses(RequestStatusUpdateCommand command) {
        List<Request> requestsToUpdate = requestRepository.findAllById(command.requestIds());

        requestsToUpdate.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConditionsNotMetException(
                        "Статус можно изменить только у заявок в состоянии ожидания. " +
                        "Текущий статус заявки " + request.getId() + ": " + request.getStatus());
            }
        });

        if (command.participantLimit() == 0 || !command.requestModeration()) {
            requestsToUpdate.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            requestRepository.saveAll(requestsToUpdate);

            return new EventRequestStatusUpdateResult(RequestDtoMapper.mapRequestToDto(requestsToUpdate), List.of());
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        long availableSlots = command.participantLimit() - command.confirmedRequests();

        for (Request request : requestsToUpdate) {
            if (availableSlots > 0 && command.status().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                availableSlots--;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        requestRepository.saveAll(requestsToUpdate);

        return new EventRequestStatusUpdateResult(
                RequestDtoMapper.mapRequestToDto(confirmedRequests),
                RequestDtoMapper.mapRequestToDto(rejectedRequests)
        );
    }

    private Request getRequestById(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request " + requestId + " not found!"));
    }

    private void validateCreation(Long userId, EventInternalDto event) {
        if (userId.equals(event.initiatorId())) {
            throw new AlreadyExistsException("Пользователя " + userId + " не может добавить запрос на участие в своем событии " + event.id());
        }

        if (!State.PUBLISHED.equals(event.state())) {
            throw new AlreadyExistsException("Нельзя участвовать в неопубликованном событии");
        }

        if (requestRepository.findByUserAndEvent(userId, event.id()).isPresent()) {
            throw new AlreadyExistsException("Для пользователя " + userId + " уже существует запрос на участие в событие " + event.id());
        }

        if (event.participantLimit() != 0 && requestRepository.countByEventAndStatus(event.id(), RequestStatus.CONFIRMED) >= event.participantLimit()) {
            throw new AlreadyExistsException("Достигнут лимит запросов на участие " + event.id());
        }
    }
}
