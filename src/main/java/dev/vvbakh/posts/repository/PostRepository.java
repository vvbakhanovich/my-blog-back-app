package dev.vvbakh.posts.repository;

import dev.vvbakh.posts.model.Post;

public interface PostRepository {
    long create(Post newPost);
}
