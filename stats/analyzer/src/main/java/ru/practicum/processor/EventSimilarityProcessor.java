package ru.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.data.service.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityProcessor implements Runnable {

    private final EventSimilarityService eventSimilarityService;
    private final Consumer<Void, EventSimilarityAvro> eventSimilarityConsumer;

    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<Void, EventSimilarityAvro> records =
                        eventSimilarityConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<Void, EventSimilarityAvro> record : records) {
                    try {
                        eventSimilarityService.save(record.value());
                    } catch (Exception e) {
                        log.error(
                                "Ошибка обработки EventSimilarity, topic={}, partition={}, offset={}, value={}",
                                record.topic(),
                                record.partition(),
                                record.offset(),
                                record.value(),
                                e
                        );
                    }
                }

                if (!records.isEmpty()) {
                    eventSimilarityConsumer.commitSync();
                    log.debug("Успешно обработано {} EventSimilarity записей", records.count());
                }
            }
        } catch (WakeupException e) {
            if (running) {
                throw e;
            }
            log.info("Получен WakeupException — корректное завершение EventSimilarityProcessor");
        } catch (Exception e) {
            log.error("Критическая ошибка в EventSimilarityProcessor", e);
        } finally {
            try {
                eventSimilarityConsumer.close();
                log.info("Kafka consumer закрыт");
            } catch (Exception e) {
                log.warn("Ошибка при закрытии Kafka consumer", e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Инициирован shutdown EventSimilarityProcessor");
        running = false;
        eventSimilarityConsumer.wakeup();
    }
}