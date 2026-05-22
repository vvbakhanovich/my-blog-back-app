package dev.vvbakh.posts.service;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
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
}
