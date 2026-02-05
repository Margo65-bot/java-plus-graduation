package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.processor.EventSimilarityProcessor;
import ru.practicum.processor.UserActionProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(AnalyzerApplication.class, args);

        final EventSimilarityProcessor eventSimilarityProcessor =
                context.getBean(EventSimilarityProcessor.class);
        final UserActionProcessor userActionProcessor =
                context.getBean(UserActionProcessor.class);

        Thread eventThread = new Thread(eventSimilarityProcessor, "event-similarity-processor");
        Thread userThread = new Thread(userActionProcessor, "user-action-processor");

        eventThread.start();
        userThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                eventThread.join();
                userThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
}
