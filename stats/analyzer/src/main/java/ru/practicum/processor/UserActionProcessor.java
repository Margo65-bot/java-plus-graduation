package ru.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.data.service.UserActionService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor implements Runnable {

    private final UserActionService userActionService;
    private final Consumer<Void, UserActionAvro> userActionConsumer;

    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<Void, UserActionAvro> records =
                        userActionConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<Void, UserActionAvro> record : records) {
                    try {
                        userActionService.save(record.value());
                    } catch (Exception e) {
                        log.error(
                                "Ошибка обработки UserAction, topic={}, partition={}, offset={}, value={}",
                                record.topic(),
                                record.partition(),
                                record.offset(),
                                record.value(),
                                e
                        );
                    }
                }

                if (!records.isEmpty()) {
                    userActionConsumer.commitSync();
                    log.debug("Успешно обработано {} UserAction записей", records.count());
                }
            }
        } catch (WakeupException e) {
            if (running) {
                throw e;
            }
            log.info("Получен WakeupException — корректное завершение UserActionProcessor");
        } catch (Exception e) {
            log.error("Критическая ошибка в UserActionProcessor", e);
        } finally {
            try {
                userActionConsumer.close();
                log.info("Kafka consumer закрыт");
            } catch (Exception e) {
                log.warn("Ошибка при закрытии Kafka consumer", e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Инициирован shutdown UserActionProcessor");
        running = false;
        userActionConsumer.wakeup();
    }
}