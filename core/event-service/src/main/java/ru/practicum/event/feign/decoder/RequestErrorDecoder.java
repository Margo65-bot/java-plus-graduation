package ru.practicum.event.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.io.InputStream;

@Slf4j
public class RequestErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = extractMessage(methodKey, response);

        return switch (response.status()) {
            case 400 -> new ValidationException(message);
            case 404 -> new NotFoundException(message);
            case 409 -> new AlreadyExistsException(message);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractMessage(String methodKey, Response response) {
        if (response.body() == null) {
            log.warn(
                    "Feign-ошибка без тела ответа. methodKey={}, status={}",
                    methodKey,
                    response.status()
            );
            return "Неизвестная ошибка при обращении к request-service";
        }

        try (InputStream is = response.body().asInputStream()) {
            ApiError apiError = objectMapper.readValue(is, ApiError.class);
            return apiError.message();
        } catch (Exception e) {
            log.error(
                    "Не удалось прочитать тело ошибки от request-service. methodKey={}, status={}",
                    methodKey,
                    response.status(),
                    e
            );
            return "Не удалось прочитать сообщение об ошибке от request-service";
        }
    }
}