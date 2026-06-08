package dev.vvbakh.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdNotMatchException extends RuntimeException {
    private long pathVariableId;
    private long postId;
}
