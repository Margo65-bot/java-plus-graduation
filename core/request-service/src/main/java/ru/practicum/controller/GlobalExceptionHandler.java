package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.ApiError;
import ru.practicum.exception.CommentStateException;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

import static ru.practicum.exception.ErrorMessages.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError(BAD_REQUEST, MISSING_PARAMETER, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError(BAD_REQUEST, TYPE_MISMATCH, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage(), e);

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();

        return new ApiError(
                BAD_REQUEST,
                VALIDATION_FAILED,
                e.getMessage(),
                errors
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCommentStateException(final CommentStateException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError(BAD_REQUEST, COMMENT_STATE_INVALID, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage(), e);
        return new ApiError(NOT_FOUND, RESOURCE_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({AlreadyExistsException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityException(final Exception e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ApiError(CONFLICT, UNIQUE_CONSTRAINT_VIOLATION, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionsNotMetException(final ConditionsNotMetException e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ApiError(CONFLICT, CONDITIONS_NOT_MET, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError(BAD_REQUEST, VALIDATION_FAILED, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError(BAD_REQUEST, VALIDATION_FAILED, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDeniedException(final AccessDeniedException e) {
        log.warn("403 {}", e.getMessage(), e);
        return new ApiError(FORBIDDEN, ACCESS_DENIED, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.warn("500 {}", e.getMessage(), e);
        return new ApiError(
                INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR,
                e.getMessage()
        );
    }
}