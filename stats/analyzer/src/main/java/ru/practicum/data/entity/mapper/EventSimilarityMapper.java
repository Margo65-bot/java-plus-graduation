package ru.practicum.data.entity.mapper;

import ru.practicum.data.entity.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public class EventSimilarityMapper {
    public static EventSimilarity mapToModel(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .eventA(eventSimilarityAvro.getEventA())
                .eventB(eventSimilarityAvro.getEventB())
                .score(eventSimilarityAvro.getScore())
                .timestamp(eventSimilarityAvro.getTimestamp())
                .build();
    }
}
