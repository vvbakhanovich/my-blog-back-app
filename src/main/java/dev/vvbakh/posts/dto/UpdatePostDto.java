package dev.vvbakh.posts.dto;

import java.util.List;

public record UpdatePostDto(long id, String title, String text, List<String> tags) {
}
