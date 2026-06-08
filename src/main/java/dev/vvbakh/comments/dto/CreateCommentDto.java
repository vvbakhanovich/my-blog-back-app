package dev.vvbakh.comments.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentDto(@NotBlank String text) {
}
