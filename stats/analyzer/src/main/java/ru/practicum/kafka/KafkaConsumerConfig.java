package ru.practicum.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.serialize.deserializer.EventSimilarityDeserializer;
import ru.practicum.serialize.deserializer.UserActionDeserializer;

import java.util.List;
import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "stats.kafka")
@Getter
@Setter
public class KafkaConsumerConfig {
    private String bootstrapServers;
    private String userActionsTopic;
    private String eventsSimilarityTopic;
    private String userActionsGroupId;
    private String eventsSimilarityGroupId;
    private String autoOffsetReset;

    @Bean
    public Consumer<Void, UserActionAvro> hubEventConsumer() {
        Properties config = setCommonProp();
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, userActionsGroupId);
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");

        Consumer<Void, UserActionAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(userActionsTopic));
        return consumer;
    }

    @Bean
    public Consumer<Void, EventSimilarityAvro> sensorsSnapshotConsumer() {
        Properties config = setCommonProp();
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, eventsSimilarityGroupId);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "60000");
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        Consumer<Void, EventSimilarityAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(eventsSimilarityTopic));
        return consumer;
    }

    private Properties setCommonProp() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        return config;
    }

}
