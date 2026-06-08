package dev.vvbakh.exception;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String contentType) {
        super("Недопустимый тип файла: " + contentType + ". Ожидается image/*");
    }
}
