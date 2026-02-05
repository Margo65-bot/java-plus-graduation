package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {

    private final Producer<Void, SpecificRecordBase> producer;

    @Value("${stats.kafka.events-similarity-topic}")
    private String eventsSimilarityTopicName;

    private static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.ACTION_VIEW, 0.4,
            ActionTypeAvro.ACTION_REGISTER, 0.8,
            ActionTypeAvro.ACTION_LIKE, 1.0
    );

    /**
     * eventId -> (userId -> maxWeight)
     */
    private final Map<Long, Map<Long, Double>> eventWeightsByUser = new ConcurrentHashMap<>();

    /**
     * eventId -> sum of weights
     */
    private final Map<Long, Double> weightSumByEvent = new ConcurrentHashMap<>();

    /**
     * min(eventA,eventB) -> (max(eventA,eventB) -> S_min)
     */
    private final Map<Long, Map<Long, Double>> minWeightsSums = new ConcurrentHashMap<>();

    /**
     * userId -> events interacted
     */
    private final Map<Long, Set<Long>> eventsByUser = new ConcurrentHashMap<>();

    @Override
    public void handleUserActionToSimilarity(UserActionAvro userActionAvro) {
        List<EventSimilarityAvro> similarities = calculateSimilarities(userActionAvro);
        if (similarities.isEmpty()) {
            return;
        }

        for (EventSimilarityAvro similarity : similarities) {
            producer.send(
                    new ProducerRecord<>(eventsSimilarityTopicName, similarity),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Ошибка при отправке EventSimilarity {}", similarity, exception);
                        }
                    }
            );
        }
    }

    private List<EventSimilarityAvro> calculateSimilarities(UserActionAvro action) {
        long eventId = action.getEventId();
        long userId = action.getUserId();

        double newWeight = WEIGHTS.get(action.getActionType());

        Map<Long, Double> weightsByUser = eventWeightsByUser.computeIfAbsent(eventId, e -> new ConcurrentHashMap<>());

        double oldWeight = weightsByUser.getOrDefault(userId, 0.0);

        if (newWeight <= oldWeight) {
            return List.of();
        }

        weightsByUser.put(userId, newWeight);

        weightSumByEvent.merge(eventId, newWeight - oldWeight, Double::sum);

        Set<Long> userEvents = eventsByUser.computeIfAbsent(userId, u -> ConcurrentHashMap.newKeySet());

        List<EventSimilarityAvro> result = new ArrayList<>();

        for (Long otherEventId : userEvents) {
            if (otherEventId.equals(eventId)) {
                continue;
            }

            double similarity = updateSimilarity(
                    eventId,
                    otherEventId,
                    userId,
                    oldWeight,
                    newWeight
            );

            result.add(toAvro(eventId, otherEventId, similarity));
        }

        userEvents.add(eventId);
        return result;
    }

    private double updateSimilarity(
            long eventA,
            long eventB,
            long userId,
            double oldWeight,
            double newWeight
    ) {
        double otherEventUserWeight =
                eventWeightsByUser
                        .getOrDefault(eventB, Map.of())
                        .getOrDefault(userId, 0.0);

        double oldContribution = Math.min(oldWeight, otherEventUserWeight);
        double newContribution = Math.min(newWeight, otherEventUserWeight);
        double diff = newContribution - oldContribution;

        double newMinSum = getMinSum(eventA, eventB) + diff;
        putMinSum(eventA, eventB, newMinSum);

        double totalA = weightSumByEvent.getOrDefault(eventA, 0.0);
        double totalB = weightSumByEvent.getOrDefault(eventB, 0.0);

        if (totalA == 0.0 || totalB == 0.0) {
            return 0.0;
        }

        return newMinSum / (Math.sqrt(totalA) * Math.sqrt(totalB));
    }

    private void putMinSum(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSums
                .computeIfAbsent(first, e -> new ConcurrentHashMap<>())
                .put(second, sum);
    }

    private double getMinSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSums
                .getOrDefault(first, Map.of())
                .getOrDefault(second, 0.0);
    }

    private EventSimilarityAvro toAvro(long eventA, long eventB, double score) {
        return EventSimilarityAvro.newBuilder()
                .setEventA(Math.min(eventA, eventB))
                .setEventB(Math.max(eventA, eventB))
                .setScore(score)
                .setTimestamp(Instant.now())
                .build();
    }
}