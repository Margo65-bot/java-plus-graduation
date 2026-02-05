package ru.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {
    protected final Producer<Void, SpecificRecordBase> producer;

    @Value("${stats.kafka.user-actions-topic}")
    private String userActionsTopicName;

    @Override
    public void handle(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(ActionTypeAvro.valueOf(userActionProto.getActionType().name()))
                .setTimestamp(
                        Instant.ofEpochSecond(
                                userActionProto.getTimestamp().getSeconds(),
                                userActionProto.getTimestamp().getNanos()
                        )
                )
                .build();
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(userActionsTopicName, userActionAvro);
        producer.send(record);
    }
}
