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

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(NotFoundException e) {
        return new ErrorMessage(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDataAccessException(DataIntegrityViolationException e) {
        return new ErrorMessage(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(IdNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIdNotMatchException(IdNotMatchException e) {
        return new ErrorMessage(Map.of("error", "Ids don't match. Path variable '" + e.getPathVariableId() +
                "' . Post id from request body '" + e.getPostId() + "'."), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> {
                            String path = v.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        v -> v.getMessage(),
                        (a, b) -> a
                ));
        return new ErrorMessage(errors, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ErrorMessage(errors, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(Exception e) {
        log.error("Unhandled exception", e);
        return new ErrorMessage(Map.of("error", "Internal server error"), 500);
    }
}
