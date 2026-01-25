package ru.practicum.event.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.io.InputStream;

public class RequestErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = extractMessage(response);

        return switch (response.status()) {
            case 400 -> new ValidationException(message);
            case 404 -> new NotFoundException(message);
            case 409 -> new AlreadyExistsException(message);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "Error from request-service";
        }

        try (InputStream is = response.body().asInputStream()) {
            ApiError apiError = objectMapper.readValue(is, ApiError.class);
            return apiError.message();
        } catch (Exception e) {
            return "Failed to parse error response from request-service";
        }
    }
}
