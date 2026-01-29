package ru.practicum.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.NotFoundException;

import java.io.InputStream;

@Slf4j
public class UserErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = extractMessage(methodKey, response);

        if (response.status() == 404) {
            return new NotFoundException(message);
        }

        return defaultDecoder.decode(methodKey, response);
    }

    private String extractMessage(String methodKey, Response response) {
        if (response.body() == null) {
            log.warn(
                    "Feign-ошибка без тела ответа. methodKey={}, status={}",
                    methodKey,
                    response.status()
            );
            return "Неизвестная ошибка при обращении к user-service";
        }

        try (InputStream is = response.body().asInputStream()) {
            ApiError apiError = objectMapper.readValue(is, ApiError.class);
            return apiError.message();
        } catch (Exception e) {
            log.error(
                    "Не удалось прочитать тело ошибки от user-service. methodKey={}, status={}",
                    methodKey,
                    response.status(),
                    e
            );
            return "Не удалось прочитать сообщение об ошибке от user-service";
        }
    }
}
