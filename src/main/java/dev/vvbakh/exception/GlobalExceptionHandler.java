package dev.vvbakh.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleNotFoundException(NotFoundException e) {
        log.info("Объект не найден", e);
        return new ErrorMessage(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleDataAccessException(DataIntegrityViolationException e) {
        log.error("Ошибка при обращении к базе данных", e);
        return new ErrorMessage(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IdNotMatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleIdNotMatchException(IdNotMatchException e) {
        log.error("Несоответствие идентификаторов при обновлении", e);
        return new ErrorMessage(Map.of("error", "Ids don't match. Path variable '" + e.getPathVariableId() +
                "' . Post id from request body '" + e.getPostId() + "'."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Нарушение ограничений", e);
        Map<String, String> errors = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> {
                            String path = v.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        v -> v.getMessage(),
                        (a, b) -> a
                ));
        return new ErrorMessage(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleValidationException(MethodArgumentNotValidException e) {
        log.error("Ошибка валидации", e);
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ErrorMessage(errors);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleInvalidFileTypeException(InvalidFileTypeException e) {
        log.error("Недопустимый тип файла", e);
        return new ErrorMessage(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(UploadFileException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUploadFileException(UploadFileException e) {
        log.error("Ошибка при загрузке файла", e);
        return new ErrorMessage(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(Exception e) {
        log.error("Произошла неизвестная ошибка", e);
        return new ErrorMessage(Map.of("error", "Internal server error"));
    }
}
