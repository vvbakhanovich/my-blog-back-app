package dev.vvbakh.posts.dto;

import java.util.List;

public record PostDto(
        long id,
        String title,
        String text,
        List<String> tags,
        long likesCount,
        long commentsCount
                      ) {
}
