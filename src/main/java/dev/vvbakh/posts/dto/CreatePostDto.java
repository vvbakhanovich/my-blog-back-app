package dev.vvbakh.posts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostDto(
        @NotBlank @Size(max = 256) String title,
        @NotBlank String text,
        @NotNull List<String> tags) {
}
