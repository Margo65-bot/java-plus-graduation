package ru.practicum.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String CONFLICT = "CONFLICT";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String MISSING_PARAMETER =
            "Ожидался обязательный параметр";

    public static final String TYPE_MISMATCH =
            "Несоответствие типов параметров";

    public static final String VALIDATION_FAILED =
            "Переданные в метод контроллера данные, не проходят проверку на валидацию";

    public static final String COMMENT_STATE_INVALID =
            "Несоответствие статуса комментария";

    public static final String RESOURCE_NOT_FOUND =
            "Обращение к несуществующему ресурсу";

    public static final String UNIQUE_CONSTRAINT_VIOLATION =
            "Нарушение ограничения уникальности";

    public static final String CONDITIONS_NOT_MET =
            "Нарушение ограничений";

    public static final String ACCESS_DENIED =
            "Доступ к этой операции запрещён";

    public static final String INTERNAL_ERROR =
            "На сервере произошла внутренняя ошибка";
}
