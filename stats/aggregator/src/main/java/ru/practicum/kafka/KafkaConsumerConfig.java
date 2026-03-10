package ru.practicum.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.serialize.deserializer.UserActionDeserializer;

import java.util.List;
import java.util.Properties;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public Consumer<Void, UserActionAvro> sensorEventConsumer(
            @Value("${stats.kafka.bootstrap-servers}") String kafkaBootstrapServers,
            @Value("${stats.kafka.user-actions-topic}") String userActionsTopicName,
            @Value("${stats.kafka.group-id}") String groupId,
            @Value("${stats.kafka.auto-offset-reset}") String autoOffsetReset,
            @Value("${stats.kafka.enable-auto-commit}") boolean enableAutoCommit
    ) {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        Consumer<Void, UserActionAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(userActionsTopicName));
        return consumer;
    }
}
