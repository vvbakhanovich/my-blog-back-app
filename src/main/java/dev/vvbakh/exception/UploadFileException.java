package dev.vvbakh.exception;


public class UploadFileException extends RuntimeException {
    public UploadFileException(String message) {
        super(message);
    }

    public UploadFileException(Throwable cause) {
        super(cause);
    }
}
