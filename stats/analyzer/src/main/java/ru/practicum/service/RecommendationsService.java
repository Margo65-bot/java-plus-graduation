package ru.practicum.service;

import ru.practicum.ewm.stats.proto.RecommendationMessages;

import java.util.stream.Stream;

public interface RecommendationsService {
    Stream<RecommendationMessages.RecommendedEventProto> getRecommendationsForUser(RecommendationMessages.UserPredictionsRequestProto request);

    Stream<RecommendationMessages.RecommendedEventProto> getSimilarEvents(RecommendationMessages.SimilarEventsRequestProto request);

    Stream<RecommendationMessages.RecommendedEventProto> getInteractionsCount(RecommendationMessages.InteractionsCountRequestProto request);
}
