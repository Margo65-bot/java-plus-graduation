package ru.practicum.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.data.entity.EventSimilarity;
import ru.practicum.data.entity.mapper.EventSimilarityMapper;
import ru.practicum.data.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    @Transactional
    public void save(EventSimilarityAvro eventSimilarityAvro) {
        EventSimilarity similarity = eventSimilarityRepository.findByEventAAndEventB(
                eventSimilarityAvro.getEventA(),
                eventSimilarityAvro.getEventB()
        ).orElseGet(() ->
                eventSimilarityRepository.save(EventSimilarityMapper.mapToModel(eventSimilarityAvro))
        );

        if (!similarity.getScore().equals(eventSimilarityAvro.getScore())) {
            similarity.setScore(eventSimilarityAvro.getScore());
        }
    }

    @Override
    public List<EventSimilarity> findNewSimilar(List<Long> eventIds, Pageable pageable) {
        return eventSimilarityRepository.findNewSimilar(eventIds, pageable);
    }

    @Override
    public List<EventSimilarity> findNeighbours(Long eventId, Pageable pageable) {
        return eventSimilarityRepository.findNeighbours(eventId, pageable);
    }
}
