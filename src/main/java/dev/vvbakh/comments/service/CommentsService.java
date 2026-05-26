package dev.vvbakh.comments.service;

import dev.vvbakh.comments.dto.CommentDto;
import dev.vvbakh.comments.dto.CreateCommentDto;

import java.util.List;

public interface CommentsService {

    CommentDto add(long postId, CreateCommentDto dto);

    List<CommentDto> getAll(long postId);

    CommentDto update(long postId, long commentId, CreateCommentDto dto);

    void delete(long postId, long commentId);
}
