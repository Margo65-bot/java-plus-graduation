package ru.practicum.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.NotFoundException;

import java.io.InputStream;

public class EventErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = extractMessage(response);

        return switch (response.status()) {
            case 404 -> new NotFoundException(message);
            case 409 -> new ConditionsNotMetException(message);
            case 403 -> new AccessDeniedException(message);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "Unknown error from event-service";
        }

        try (InputStream is = response.body().asInputStream()) {
            ApiError error = objectMapper.readValue(is, ApiError.class);
            return error.message();
        } catch (Exception e) {
            return "Failed to read error message from event-service";
        }
    }
}
