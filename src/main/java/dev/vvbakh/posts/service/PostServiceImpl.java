package dev.vvbakh.posts.service;

import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.repository.PostRepository;
import dev.vvbakh.tags.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagRepository tagService;

    @Override
    @Transactional
    public Post create(Post newPost) {
        log.debug("Добавление поста с заголовком '{}'", newPost.title());
        final long createdPostId = postRepository.create(newPost);
        tagService.saveAll(createdPostId, newPost.tags());
        return null;
    }
}
