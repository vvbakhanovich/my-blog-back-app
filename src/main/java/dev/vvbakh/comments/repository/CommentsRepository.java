package dev.vvbakh.comments.repository;

import dev.vvbakh.comments.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository {

    long create(Comment comment);

    Optional<Comment> getById(long commentId);

    List<Comment> getAllByPostId(long postId);

    void update(Comment comment);

    void delete(long commentId);

    long countByPostId(long postId);
}
