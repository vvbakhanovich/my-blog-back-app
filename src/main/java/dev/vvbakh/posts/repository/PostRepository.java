package dev.vvbakh.posts.repository;

import dev.vvbakh.posts.model.Post;

import java.util.Optional;

public interface PostRepository {
    long create(Post newPost);

    Optional<Post> getById(long postId);

    void update(Post updated);
}
