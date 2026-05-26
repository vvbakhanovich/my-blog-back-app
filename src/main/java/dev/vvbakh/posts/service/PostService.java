package dev.vvbakh.posts.service;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.dto.PostsPageDto;
import dev.vvbakh.posts.dto.UpdatePostDto;

public interface PostService {
    PostDto create(CreatePostDto dto);

    PostDto getById(long postId);

    PostDto updatePost(long postId, UpdatePostDto updatedPost);

    PostsPageDto getAll(String search, int pageNumber, int pageSize);

    void deletePost(long postId);
}
