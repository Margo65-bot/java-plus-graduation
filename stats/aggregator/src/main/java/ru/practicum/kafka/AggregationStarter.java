package ru.practicum.kafka;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.EventSimilarityService;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final EventSimilarityService eventSimilarityService;
    private final Producer<Void, SpecificRecordBase> producer;
    private final Consumer<Void, UserActionAvro> sensorConsumer;

    private volatile boolean running = true;

    public void start() {
        try {
            while (running) {
                ConsumerRecords<Void, UserActionAvro> records = sensorConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<Void, UserActionAvro> record : records) {
                    eventSimilarityService.handleUserActionToSimilarity(record.value());
                }

                if (!records.isEmpty()) {
                    sensorConsumer.commitSync();
                }
            }

        } catch (WakeupException e) {
            log.info("Получен WakeupException - завершение работы");
        } catch (Exception e) {
            log.error("Ошибка во время агрегации данных", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        sensorConsumer.wakeup();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        closeResources();
    }

    private void closeResources() {
        try {
            producer.flush();
            sensorConsumer.commitSync();
        } catch (Exception e) {
            log.warn("Ошибка при завершении работы ресурсов", e);
        } finally {
            sensorConsumer.close();
            producer.close();
        }
    }
}
