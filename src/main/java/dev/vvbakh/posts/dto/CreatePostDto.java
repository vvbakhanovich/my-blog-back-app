package dev.vvbakh.posts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePostDto(
        @NotBlank String title,
        @NotBlank String text,
        @NotNull List<String> tags) {
}
