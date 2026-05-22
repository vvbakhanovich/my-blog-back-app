package dev.vvbakh.posts.dto;

import java.util.List;

public record CreatePostDto(
        String title,
        String text,
        List<String> tags) {
}
