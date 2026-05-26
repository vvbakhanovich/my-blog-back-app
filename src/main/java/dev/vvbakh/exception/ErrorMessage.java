package dev.vvbakh.exception;

import java.util.Map;

public record ErrorMessage(Map<String, String> errors, int statusCode) {
}
