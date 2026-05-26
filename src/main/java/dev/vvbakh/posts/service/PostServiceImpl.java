package dev.vvbakh.posts.service;

import dev.vvbakh.exception.NotFoundException;
import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.dto.UpdatePostDto;
import dev.vvbakh.posts.mapper.PostMapper;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.repository.PostRepository;
import dev.vvbakh.tags.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostDto create(CreatePostDto dto) {
        log.debug("Добавление поста с заголовком '{}'", dto.title());
        long postId = postRepository.create(postMapper.toModel(dto));
        tagRepository.saveAll(postId, dto.tags());
        return getById(postId);
    }

    @Override
    public PostDto getById(long postId) {
        log.debug("Получение поста с id '{}'", postId);
        final var post = getPostOrThrow(postId);
        final List<String> tags = tagRepository.getAllByPostId(postId);
        return postMapper.toDto(post, tags);
    }

    @Override
    @Transactional
    public PostDto updatePost(long postId, UpdatePostDto updatedPost) {
        log.info("Обновление поста с идентификатором '{}'.", postId);
        getPostOrThrow(postId);
        final Post updated = postMapper.toModel(updatedPost);
        postRepository.update(updated);
        tagRepository.updateAll(postId, updatedPost.tags());
        return getById(postId);
    }

    private Post getPostOrThrow(long postId) {
        return postRepository.getById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с id '" + postId + "' не найден"));
    }
}
