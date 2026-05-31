package dev.vvbakh.posts.repository;

import dev.vvbakh.RepositoryTestConfiguration;
import dev.vvbakh.posts.model.Post;
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

    @Test
    @DisplayName("обновлять данные поста")
    void update_shouldUpdatePost() {
        long id = postRepository.create(new Post(null, "Old Title", "Old Content", 0));

        postRepository.update(new Post(id, "New Title", "New Content", 0));

        Post found = postRepository.getById(id).orElseThrow();
        assertEquals("New Title", found.title());
        assertEquals("New Content", found.content());
    }

    @Test
    @DisplayName("возвращать посты, совпадающие по title")
    void getAll_shouldReturnPostsMatchingTitle() {
        postRepository.create(new Post(null, "Java Guide", "Some content", 0));
        postRepository.create(new Post(null, "Spring Guide", "Other content", 0));

        List<Post> result = postRepository.getAll("java", 1, 10);

        assertEquals(1, result.size());
        assertEquals("Java Guide", result.get(0).title());
    }

    @Test
    @DisplayName("возвращать посты, совпадающие по content")
    void getAll_shouldReturnPostsMatchingContent() {
        postRepository.create(new Post(null, "Post One", "Content about Hibernate", 0));
        postRepository.create(new Post(null, "Post Two", "Content about Redis", 0));

        List<Post> result = postRepository.getAll("Hibernate", 1, 10);

        assertEquals(1, result.size());
        assertEquals("Post One", result.get(0).title());
    }

    @Test
    @DisplayName("возвращать пустой список если ничего не совпало")
    void getAll_shouldReturnEmptyListWhenNoMatch() {
        postRepository.create(new Post(null, "Java Post", "Java content", 0));

        List<Post> result = postRepository.getAll("python", 1, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("возвращать правильную страницу при пагинации")
    void getAll_shouldReturnCorrectPage() {
        for (int i = 1; i <= 4; i++) {
            postRepository.create(new Post(null, "Post " + i, "Content", 0));
        }

        List<Post> page1 = postRepository.getAll("Post", 1, 2);
        List<Post> page2 = postRepository.getAll("Post", 2, 2);

        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertTrue(page1.stream().noneMatch(p -> page2.stream().anyMatch(p2 -> p2.id().equals(p.id()))));
    }

    @Test
    @DisplayName("возвращать все посты при пустой строке поиска")
    void getAll_shouldReturnAllPostsWhenSearchIsEmpty() {
        postRepository.create(new Post(null, "Java Post", "Java content", 0));
        postRepository.create(new Post(null, "Spring Post", "Spring content", 0));

        List<Post> result = postRepository.getAll("", 1, 10);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("считать количество постов по поисковой строке")
    void countAll_shouldReturnMatchingCount() {
        postRepository.create(new Post(null, "Java Post", "Java content", 0));
        postRepository.create(new Post(null, "Spring Post", "Spring content", 0));
        postRepository.create(new Post(null, "Other Post", "Other content", 0));

        long count = postRepository.countAll("java");

        assertEquals(1, count);
    }

    @Test
    @DisplayName("увеличивать likes_count на 1 и возвращать новое значение")
    void incrementLikes_shouldIncrementAndReturnUpdatedCount() {
        long id = postRepository.create(new Post(null, "Title", "Content", 0));

        long likes = postRepository.incrementLikes(id);

        assertEquals(1L, likes);
    }

    @Test
    @DisplayName("удалять пост по идентификатору")
    void delete_shouldRemovePost() {
        long id = postRepository.create(new Post(null, "To Delete", "Content", 0));

        postRepository.delete(id);

        assertTrue(postRepository.getById(id).isEmpty());
    }
}
