package dev.vvbakh.comments.service;

import dev.vvbakh.comments.dto.CommentDto;
import dev.vvbakh.comments.dto.CreateCommentDto;
import dev.vvbakh.comments.model.Comment;
import dev.vvbakh.comments.repository.CommentsRepository;
import dev.vvbakh.exception.NotFoundException;
import dev.vvbakh.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final PostService postService;

    @Override
    public CommentDto add(long postId, CreateCommentDto dto) {
        log.info("Добавление комментария к посту с id '{}'", postId);
        postService.getById(postId);
        long commentId = commentsRepository.create(new Comment(null, dto.text(), postId));
        return toDto(commentsRepository.getById(commentId).orElseThrow());
    }

    @Override
    public List<CommentDto> getAll(long postId) {
        log.debug("Получение комментариев поста с id '{}'", postId);
        postService.getById(postId);
        return commentsRepository.getAllByPostId(postId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public CommentDto update(long postId, long commentId, CreateCommentDto dto) {
        log.info("Обновление комментария с id '{}' поста '{}'", commentId, postId);
        postService.getById(postId);
        Comment existing = getCommentOrThrow(commentId);
        commentsRepository.update(new Comment(existing.id(), dto.text(), existing.postId()));
        return toDto(commentsRepository.getById(commentId).orElseThrow());
    }

    @Override
    public void delete(long postId, long commentId) {
        log.info("Удаление комментария с id '{}' поста '{}'", commentId, postId);
        postService.getById(postId);
        getCommentOrThrow(commentId);
        commentsRepository.delete(commentId);
    }

    private Comment getCommentOrThrow(long commentId) {
        return commentsRepository.getById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id '" + commentId + "' не найден"));
    }

    private CommentDto toDto(Comment comment) {
        return new CommentDto(comment.id(), comment.content(), comment.postId());
    }
}
