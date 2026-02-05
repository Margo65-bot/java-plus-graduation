package ru.practicum.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.RecommendationMessages;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AnalyzerGrpcClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public Stream<RecommendationMessages.RecommendedEventProto> getRecommendationsForUser(
            long userId,
            int maxResults
    ) {
        RecommendationMessages.UserPredictionsRequestProto request =
                RecommendationMessages.UserPredictionsRequestProto.newBuilder()
                        .setUserId(userId)
                        .setMaxResults(maxResults)
                        .build();

        try {
            Iterator<RecommendationMessages.RecommendedEventProto> iterator = client.getRecommendationsForUser(request);
            return asStream(iterator);
        } catch (StatusRuntimeException e) {
            log.error("gRPC ошибка при вызове getRecommendationsForUser: {}", e.getStatus(), e);
            return Stream.empty();
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при вызове getRecommendationsForUser", e);
            return Stream.empty();
        }
    }

    public Stream<RecommendationMessages.RecommendedEventProto> getSimilarEvents(
            long eventId,
            long userId,
            int maxResults
    ) {
        RecommendationMessages.SimilarEventsRequestProto request =
                RecommendationMessages.SimilarEventsRequestProto.newBuilder()
                        .setEventId(eventId)
                        .setUserId(userId)
                        .setMaxResults(maxResults)
                        .build();

        try {
            Iterator<RecommendationMessages.RecommendedEventProto> iterator = client.getSimilarEvents(request);
            return asStream(iterator);
        } catch (StatusRuntimeException e) {
            log.error("gRPC ошибка при вызове getSimilarEvents: {}", e.getStatus(), e);
            return Stream.empty();
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при вызове getSimilarEvents", e);
            return Stream.empty();
        }
    }

    public Stream<RecommendationMessages.RecommendedEventProto> getInteractionsCount(
            Iterable<Long> eventIds
    ) {
        RecommendationMessages.InteractionsCountRequestProto request =
                RecommendationMessages.InteractionsCountRequestProto.newBuilder()
                        .addAllEventId(eventIds)
                        .build();

        try {
            Iterator<RecommendationMessages.RecommendedEventProto> iterator = client.getInteractionsCount(request);
            return asStream(iterator);
        } catch (StatusRuntimeException e) {
            log.error("gRPC ошибка при вызове getInteractionsCount: {}", e.getStatus(), e);
            return Stream.empty();
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при вызове getInteractionsCount", e);
            return Stream.empty();
        }
    }

    private <T> Stream<T> asStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}