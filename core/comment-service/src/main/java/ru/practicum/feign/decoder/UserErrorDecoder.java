package ru.practicum.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.NotFoundException;

import java.io.InputStream;

public class UserErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = extractMessage(response);

        if (response.status() == 404) {
            return new NotFoundException(message);
        }

        return defaultDecoder.decode(methodKey, response);
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "Error from user-service";
        }

        try (InputStream is = response.body().asInputStream()) {
            ApiError apiError = objectMapper.readValue(is, ApiError.class);
            return apiError.message();
        } catch (Exception e) {
            return "Failed to parse error response from user-service";
        }
    }
}
