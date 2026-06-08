package dev.vvbakh.posts.dto;

import java.util.List;

public record PostsPageDto(List<PostDto> posts, boolean hasPrev, boolean hasNext, long lastPage) {
}
