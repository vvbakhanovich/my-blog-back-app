package dev.vvbakh.tags.repository;

import dev.vvbakh.RepositoryTestConfiguration;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("JdbcTagRepository должен")
class JdbcTagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("сохранять теги для поста одной операцией")
    void saveAll_shouldPersistTagsForPost() {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));
        List<String> tags = List.of("java", "spring");

        tagRepository.saveAll(postId, tags);
        List<String> result = tagRepository.getAllByPostId(postId);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(tags));
    }

    @Test
    @DisplayName("возвращать пустой список если у поста нет тегов")
    void getAllByPostId_shouldReturnEmptyListWhenNoTags() {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));

        List<String> result = tagRepository.getAllByPostId(postId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("не сохранять дубликаты тегов в одном вызове")
    void saveAll_shouldDeduplicateTagsWithinSingleCall() {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));

        tagRepository.saveAll(postId, List.of("java", "spring", "java"));
        List<String> result = tagRepository.getAllByPostId(postId);

        assertEquals(2, result.size());
    }
}
