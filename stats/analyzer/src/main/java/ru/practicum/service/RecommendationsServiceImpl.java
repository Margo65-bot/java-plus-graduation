package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.data.entity.EventSimilarity;
import ru.practicum.data.entity.UserAction;
import ru.practicum.data.service.EventSimilarityService;
import ru.practicum.data.service.UserActionService;
import ru.practicum.ewm.stats.proto.RecommendationMessages;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.util.ActionWeights.WEIGHTS;

@Service
@RequiredArgsConstructor
public class RecommendationsServiceImpl implements RecommendationsService {
    private final UserActionService userActionService;
    private final EventSimilarityService eventSimilarityService;

    @Override
    public Stream<RecommendationMessages.RecommendedEventProto> getRecommendationsForUser(
            RecommendationMessages.UserPredictionsRequestProto request
    ) {

        long userId = request.getUserId();
        int limit = request.getMaxResults();

        List<Long> userEventIds = userActionService.findByUserId(
                userId,
                PageRequest.of(
                        0, limit, Sort.by(Sort.Direction.DESC, "timestamp")
                )
        );

        if (userEventIds.isEmpty()) {
            return Stream.empty();
        }

        PageRequest similarityPage = PageRequest.of(
                0, limit, Sort.by(Sort.Direction.DESC, "score")
        );

        List<EventSimilarity> similarities = eventSimilarityService.findNewSimilar(userEventIds, similarityPage);

        Map<Long, Double> predictedScores = predictScores(similarities, userEventIds);

        return mapToProto(predictedScores);
    }

    @Override
    public Stream<RecommendationMessages.RecommendedEventProto> getSimilarEvents(
            RecommendationMessages.SimilarEventsRequestProto request
    ) {

        long eventId = request.getEventId();
        int limit = request.getMaxResults();

        List<EventSimilarity> similarities = eventSimilarityService.findNeighbours(eventId, Pageable.unpaged());

        Set<Long> userEventIds = userActionService.findByUserIdExcludeEventId(request.getUserId(), eventId);

        List<EventSimilarity> filtered = similarities
                .stream()
                .filter(s ->
                        !(userEventIds.contains(s.getEventA())
                                && userEventIds.contains(s.getEventB()))
                )
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(limit)
                .toList();

        return mapToProto(filtered, eventId);
    }

    @Override
    public Stream<RecommendationMessages.RecommendedEventProto> getInteractionsCount(
            RecommendationMessages.InteractionsCountRequestProto request
    ) {

        Map<Long, Double> sumOfWeightsByEvent = new HashMap<>();

        List<UserAction> userActions = userActionService.findByEventIdIn(
                new HashSet<>(request.getEventIdList())
        );

        for (UserAction action : userActions) {
            long eventId = action.getEventId();
            double weight = WEIGHTS.getOrDefault(action.getActionType(), 0.0);
            sumOfWeightsByEvent.merge(eventId, weight, Double::sum);
        }

        return mapToProto(sumOfWeightsByEvent);
    }

    private Map<Long, Double> predictScores(
            List<EventSimilarity> similarities,
            List<Long> userEvents
    ) {
        Map<Long, Double> scoreByEvent = new HashMap<>();

        Map<Long, List<EventSimilarity>> byCandidate = similarities
                .stream()
                .collect(Collectors.groupingBy(s ->
                        userEvents.contains(s.getEventA())
                                ? s.getEventB()
                                : s.getEventA()
                ));

        for (Map.Entry<Long, List<EventSimilarity>> entry : byCandidate.entrySet()) {
            long candidate = entry.getKey();

            PageRequest neighboursPage = PageRequest.of(
                    0, 5, Sort.by(Sort.Direction.DESC, "score")
            );

            List<EventSimilarity> neighbours = eventSimilarityService.findNeighbours(candidate, neighboursPage);

            List<Long> neighbourIds = neighbours
                    .stream()
                    .map(n -> n.getEventA() == candidate ? n.getEventB() : n.getEventA())
                    .toList();

            List<UserAction> userActions = userActionService.findByEventIdIn(new HashSet<>(neighbourIds));

            double predictedScore = calculatePredictedScore(neighbours, userActions, candidate);

            scoreByEvent.put(candidate, predictedScore);
        }

        return scoreByEvent;
    }

    private double calculatePredictedScore(
            List<EventSimilarity> neighbours,
            List<UserAction> userActions,
            long candidateId
    ) {
        Map<Long, Double> weightByEvent = userActions
                .stream()
                .collect(Collectors.toMap(
                        UserAction::getEventId,
                        ua -> WEIGHTS.getOrDefault(ua.getActionType(), 0.0),
                        Double::max
                ));

        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (EventSimilarity neighbour : neighbours) {
            long neighbourId = neighbour.getEventA() == candidateId
                    ? neighbour.getEventB()
                    : neighbour.getEventA();

            double weight = weightByEvent.getOrDefault(neighbourId, 0.0);
            double similarity = neighbour.getScore();

            weightedSum += weight * similarity;
            similaritySum += similarity;
        }

        return similaritySum == 0.0 ? 0.0 : weightedSum / similaritySum;
    }

    private Stream<RecommendationMessages.RecommendedEventProto> mapToProto(
            List<EventSimilarity> similarities,
            long currentEventId
    ) {

        return similarities.stream()
                .map(s -> {
                    long recommended = s.getEventA() == currentEventId
                            ? s.getEventB()
                            : s.getEventA();

                    return RecommendationMessages.RecommendedEventProto.newBuilder()
                            .setEventId(recommended)
                            .setScore(s.getScore())
                            .build();
                });
    }

    private Stream<RecommendationMessages.RecommendedEventProto> mapToProto(Map<Long, Double> scores) {

        return scores.entrySet()
                .stream()
                .map(e ->
                        RecommendationMessages.RecommendedEventProto.newBuilder()
                                .setEventId(e.getKey())
                                .setScore(e.getValue())
                                .build()
                );
    }
}