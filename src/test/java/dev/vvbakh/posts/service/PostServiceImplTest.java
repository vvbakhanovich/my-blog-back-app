package dev.vvbakh.posts.service;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.dto.PostsPageDto;
import dev.vvbakh.posts.dto.UpdatePostDto;
import dev.vvbakh.posts.mapper.PostMapper;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.repository.PostRepository;
import dev.vvbakh.tags.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.vvbakh.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostServiceImpl должен")
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("создавать пост с тегами и возвращать DTO созданного поста")
    void create_shouldCreatePostAndSaveTagsAndReturnDto() {
        CreatePostDto dto = new CreatePostDto("Title", "Content", List.of("java", "spring"));
        Post mappedPost = new Post(null, "Title", "Content", 0);
        Post savedPost = new Post(1L, "Title", "Content", 0);
        List<String> tags = List.of("java", "spring");
        PostDto expectedDto = new PostDto(1L, "Title", "Content", tags, 0, 0);

        when(postMapper.toModel(dto)).thenReturn(mappedPost);
        when(postRepository.create(mappedPost)).thenReturn(1L);
        when(postRepository.getById(1L)).thenReturn(Optional.of(savedPost));
        when(tagRepository.getAllByPostId(1L)).thenReturn(tags);
        when(postMapper.toDto(savedPost, tags)).thenReturn(expectedDto);

        PostDto result = postService.create(dto);

        assertEquals(expectedDto, result);
        verify(postRepository).create(mappedPost);
        verify(tagRepository).saveAll(1L, dto.tags());
    }

    @Test
    @DisplayName("возвращать пост с тегами по идентификатору")
    void getById_shouldReturnPostDtoWithTags() {
        Post post = new Post(1L, "Title", "Content", 5L);
        List<String> tags = List.of("java");
        PostDto expectedDto = new PostDto(1L, "Title", "Content", tags, 5L, 0);

        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(tagRepository.getAllByPostId(1L)).thenReturn(tags);
        when(postMapper.toDto(post, tags)).thenReturn(expectedDto);

        PostDto result = postService.getById(1L);

        assertEquals(expectedDto, result);
        verify(postRepository).getById(1L);
        verify(tagRepository).getAllByPostId(1L);
    }

    @Test
    @DisplayName("бросать NotFoundException если пост не найден")
    void getById_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.getById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.getById(999L));
    }

    @Test
    @DisplayName("обновлять пост с тегами и возвращать обновлённый DTO")
    void updatePost_shouldUpdatePostAndTagsAndReturnDto() {
        UpdatePostDto dto = new UpdatePostDto(1L, "New Title", "New Content", List.of("tag1"));
        Post post = new Post(1L, "New Title", "New Content", 0);
        List<String> tags = List.of("tag1");
        PostDto expectedDto = new PostDto(1L, "New Title", "New Content", tags, 0, 0);

        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toModel(dto)).thenReturn(post);
        when(tagRepository.getAllByPostId(1L)).thenReturn(tags);
        when(postMapper.toDto(post, tags)).thenReturn(expectedDto);

        PostDto result = postService.updatePost(1L, dto);

        assertEquals(expectedDto, result);
        verify(postRepository).update(post);
        verify(tagRepository).updateAll(1L, dto.tags());
    }

    @Test
    @DisplayName("инкрементировать лайки и возвращать обновлённое значение")
    void incrementLikes_shouldReturnUpdatedLikesCount() {
        Post post = new Post(1L, "Title", "Content", 0);
        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(postRepository.incrementLikes(1L)).thenReturn(1L);

        long result = postService.incrementLikes(1L);

        assertEquals(1L, result);
        verify(postRepository).incrementLikes(1L);
    }

    @Test
    @DisplayName("бросать NotFoundException при инкременте лайков несуществующего поста")
    void incrementLikes_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.getById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.incrementLikes(999L));
    }

    @Test
    @DisplayName("удалять пост по идентификатору")
    void deletePost_shouldCallRepositoryDelete() {
        Post post = new Post(1L, "Title", "Content", 0);
        when(postRepository.getById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(postRepository).delete(1L);
    }

    @Test
    @DisplayName("бросать NotFoundException при удалении несуществующего поста")
    void deletePost_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.getById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.deletePost(999L));
    }

    @Test
    @DisplayName("бросать NotFoundException при обновлении несуществующего поста")
    void updatePost_shouldThrowNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.getById(999L)).thenReturn(Optional.empty());
        UpdatePostDto dto = new UpdatePostDto(999L, "Title", "Content", List.of());

        assertThrows(NotFoundException.class, () -> postService.updatePost(999L, dto));
    }

    @Test
    @DisplayName("возвращать PostsPageDto с правильными флагами пагинации")
    void getAll_shouldReturnPostsPageWithCorrectPaginationFlags() {
        Post post = new Post(1L, "Java Post", "Content", 0);
        List<String> tags = List.of("java");
        PostDto postDto = new PostDto(1L, "Java Post", "Content", tags, 0, 0);

        when(postRepository.getAll("java", 1, 2)).thenReturn(List.of(post));
        when(postRepository.countAll("java")).thenReturn(3L);
        when(tagRepository.getAllByPostId(1L)).thenReturn(tags);
        when(postMapper.toDto(post, tags)).thenReturn(postDto);

        PostsPageDto result = postService.getAll("java", 1, 2);

        assertEquals(1, result.posts().size());
        assertEquals(false, result.hasPrev());
        assertEquals(true, result.hasNext());
        assertEquals(2L, result.lastPage());
    }

    @Test
    @DisplayName("обрезать текст поста до 128 символов с многоточием")
    void getAll_shouldTruncateTextLongerThan128Chars() {
        String longText = "A".repeat(200);
        Post post = new Post(1L, "Post", longText, 0);
        PostDto postDto = new PostDto(1L, "Post", longText, List.of(), 0, 0);

        when(postRepository.getAll("Post", 1, 10)).thenReturn(List.of(post));
        when(postRepository.countAll("Post")).thenReturn(1L);
        when(tagRepository.getAllByPostId(1L)).thenReturn(List.of());
        when(postMapper.toDto(post, List.of())).thenReturn(postDto);

        PostsPageDto result = postService.getAll("Post", 1, 10);

        String expectedText = "A".repeat(128) + "…";
        assertEquals(expectedText, result.posts().get(0).text());
    }
}
