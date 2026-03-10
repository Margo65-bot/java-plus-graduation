package ru.practicum.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface EventSimilarityService {
    void handleUserActionToSimilarity(UserActionAvro userActionAvro);
}
