package dev.vvbakh.posts.repository;

import dev.vvbakh.WebConfiguration;
import dev.vvbakh.posts.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("JdbcPostRepository должен")
class JdbcPostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("создавать пост и возвращать сгенерированный идентификатор")
    void create_shouldReturnGeneratedId() {
        Post newPost = new Post(null, "Test Title", "Test Content", 0);

        long id = postRepository.create(newPost);

        assertTrue(id > 0);
    }

    @Test
    @DisplayName("возвращать сохранённый пост по идентификатору")
    void getById_shouldReturnSavedPost() {
        long id = postRepository.create(new Post(null, "Test Title", "Test Content", 0));

        Post found = postRepository.getById(id).orElseThrow();

        assertEquals("Test Title", found.title());
        assertEquals("Test Content", found.content());
        assertEquals(0L, found.likesCount());
    }
}
