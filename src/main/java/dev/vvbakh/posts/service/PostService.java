package dev.vvbakh.posts.service;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;

public interface PostService {
    PostDto create(CreatePostDto dto);
    PostDto getById(long postId);
}
