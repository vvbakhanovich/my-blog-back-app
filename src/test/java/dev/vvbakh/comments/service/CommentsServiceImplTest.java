package dev.vvbakh.comments.service;

import dev.vvbakh.comments.dto.CommentDto;
import dev.vvbakh.comments.dto.CreateCommentDto;
import dev.vvbakh.comments.model.Comment;
import dev.vvbakh.comments.repository.CommentsRepository;
import dev.vvbakh.exception.NotFoundException;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentsServiceImpl должен")
class CommentsServiceImplTest {

    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private PostService postService;

    @InjectMocks
    private CommentsServiceImpl commentsService;

    private PostDto stubPost(long postId) {
        PostDto dto = new PostDto(postId, "Title", "Content", List.of(), 0, 0);
        when(postService.getById(postId)).thenReturn(dto);
        return dto;
    }

    @Test
    @DisplayName("добавлять комментарий и возвращать DTO")
    void add_shouldCreateCommentAndReturnDto() {
        stubPost(1L);
        Comment saved = new Comment(10L, "Hello", 1L);
        when(commentsRepository.create(new Comment(null, "Hello", 1L))).thenReturn(10L);
        when(commentsRepository.getById(10L)).thenReturn(Optional.of(saved));

        CommentDto result = commentsService.add(1L, new CreateCommentDto("Hello"));

        assertEquals(10L, result.id());
        assertEquals("Hello", result.text());
        assertEquals(1L, result.postId());
    }

    @Test
    @DisplayName("бросать NotFoundException при добавлении комментария к несуществующему посту")
    void add_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
        when(postService.getById(999L)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class,
                () -> commentsService.add(999L, new CreateCommentDto("Text")));
    }

    @Test
    @DisplayName("возвращать список комментариев поста")
    void getAll_shouldReturnCommentDtos() {
        stubPost(1L);
        when(commentsRepository.getAllByPostId(1L)).thenReturn(List.of(
                new Comment(1L, "First", 1L),
                new Comment(2L, "Second", 1L)
        ));

        List<CommentDto> result = commentsService.getAll(1L);

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).text());
    }

    @Test
    @DisplayName("бросать NotFoundException при получении комментариев несуществующего поста")
    void getAll_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
        when(postService.getById(999L)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> commentsService.getAll(999L));
    }

    @Test
    @DisplayName("обновлять комментарий и возвращать обновлённый DTO")
    void update_shouldUpdateCommentAndReturnDto() {
        stubPost(1L);
        Comment existing = new Comment(5L, "Old", 1L);
        Comment updated = new Comment(5L, "New", 1L);
        when(commentsRepository.getById(5L)).thenReturn(Optional.of(existing)).thenReturn(Optional.of(updated));

        CommentDto result = commentsService.update(1L, 5L, new CreateCommentDto("New"));

        verify(commentsRepository).update(new Comment(5L, "New", 1L));
        assertEquals("New", result.text());
    }

    @Test
    @DisplayName("бросать NotFoundException при обновлении несуществующего комментария")
    void update_shouldThrowNotFoundExceptionWhenCommentDoesNotExist() {
        stubPost(1L);
        when(commentsRepository.getById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentsService.update(1L, 999L, new CreateCommentDto("Text")));
    }

    @Test
    @DisplayName("удалять комментарий")
    void delete_shouldCallRepositoryDelete() {
        stubPost(1L);
        Comment comment = new Comment(5L, "Hello", 1L);
        when(commentsRepository.getById(5L)).thenReturn(Optional.of(comment));

        commentsService.delete(1L, 5L);

        verify(commentsRepository).delete(5L);
    }

    @Test
    @DisplayName("бросать NotFoundException при удалении несуществующего комментария")
    void delete_shouldThrowNotFoundExceptionWhenCommentDoesNotExist() {
        stubPost(1L);
        when(commentsRepository.getById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentsService.delete(1L, 999L));
    }
}
