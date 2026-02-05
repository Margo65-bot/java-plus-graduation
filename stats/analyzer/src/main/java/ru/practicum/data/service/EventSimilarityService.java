package ru.practicum.data.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.data.entity.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;

public interface EventSimilarityService {
    void save(EventSimilarityAvro eventSimilarityAvro);

    List<EventSimilarity> findNewSimilar(List<Long> eventIds, Pageable pageable);

    List<EventSimilarity> findNeighbours(Long eventId, Pageable pageable);
}
