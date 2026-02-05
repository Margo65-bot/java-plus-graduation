package ru.practicum.client;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Slf4j
@Service
public class CollectorGrpcClient {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void collectUserAction(
            long userId,
            long eventId,
            ActionType actionType,
            Instant timestamp
    ) {
        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.valueOf(actionType.name()))
                .setTimestamp(toProtoTimestamp(timestamp))
                .build();

        try {
            client.collectUserAction(request);
        } catch (StatusRuntimeException e) {
            log.error("gRPC ошибка при отправке collectUserAction: {}", e.getStatus(), e);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при отправке collectUserAction", e);
        }
    }

    private Timestamp toProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
