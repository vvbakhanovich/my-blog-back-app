package dev.vvbakh.posts.model;

import java.util.List;

public record Post(
        long id,
        String title,
        String content,
        List<String> tags,
        long likesCount,
        long commentsCount
) {}
