package ru.practicum.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.RecommendationMessages;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.service.RecommendationsService;

import java.util.stream.Stream;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationsService recommendationsService;

    @Override
    public void getRecommendationsForUser(
            RecommendationMessages.UserPredictionsRequestProto request,
            StreamObserver<RecommendationMessages.RecommendedEventProto> responseObserver
    ) {
        handleStreamResponse(
                recommendationsService.getRecommendationsForUser(request),
                responseObserver
        );
    }

    @Override
    public void getSimilarEvents(
            RecommendationMessages.SimilarEventsRequestProto request,
            StreamObserver<RecommendationMessages.RecommendedEventProto> responseObserver
    ) {
        handleStreamResponse(
                recommendationsService.getSimilarEvents(request),
                responseObserver
        );
    }

    @Override
    public void getInteractionsCount(
            RecommendationMessages.InteractionsCountRequestProto request,
            StreamObserver<RecommendationMessages.RecommendedEventProto> responseObserver
    ) {
        handleStreamResponse(
                recommendationsService.getInteractionsCount(request),
                responseObserver
        );
    }

    private void handleStreamResponse(
            Stream<RecommendationMessages.RecommendedEventProto> stream,
            StreamObserver<RecommendationMessages.RecommendedEventProto> responseObserver
    ) {
        try (Stream<RecommendationMessages.RecommendedEventProto> safeStream = stream) {
            safeStream.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
