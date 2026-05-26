package dev.vvbakh.posts.repository;

import dev.vvbakh.posts.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    long create(Post newPost);

    Optional<Post> getById(long postId);

    void update(Post updated);

    List<Post> getAll(String search, int pageNumber, int pageSize);

    long countAll(String search);
}
