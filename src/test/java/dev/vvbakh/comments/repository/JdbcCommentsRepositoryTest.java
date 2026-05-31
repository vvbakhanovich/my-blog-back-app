package dev.vvbakh.comments.repository;

import dev.vvbakh.RepositoryTestConfiguration;
import dev.vvbakh.comments.model.Comment;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("JdbcCommentsRepository должен")
class JdbcCommentsRepositoryTest {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PostRepository postRepository;

    private long postId;

    @BeforeEach
    void setup() {
        postId = postRepository.create(new Post(null, "Post", "Content", 0));
    }

    @Test
    @DisplayName("создавать комментарий и возвращать сгенерированный id")
    void create_shouldReturnGeneratedId() {
        long id = commentsRepository.create(new Comment(null, "Hello", postId));
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("возвращать комментарий по id")
    void getById_shouldReturnComment() {
        long id = commentsRepository.create(new Comment(null, "Hello", postId));
        var comment = commentsRepository.getById(id);
        assertTrue(comment.isPresent());
        assertEquals("Hello", comment.get().content());
        assertEquals(postId, comment.get().postId());
    }

    @Test
    @DisplayName("возвращать пустой Optional если комментарий не найден")
    void getById_shouldReturnEmptyWhenNotFound() {
        assertTrue(commentsRepository.getById(Long.MAX_VALUE).isEmpty());
    }

    @Test
    @DisplayName("возвращать все комментарии поста")
    void getAllByPostId_shouldReturnAllComments() {
        commentsRepository.create(new Comment(null, "First", postId));
        commentsRepository.create(new Comment(null, "Second", postId));
        List<Comment> comments = commentsRepository.getAllByPostId(postId);
        assertEquals(2, comments.size());
    }

    @Test
    @DisplayName("обновлять содержимое комментария")
    void update_shouldChangeContent() {
        long id = commentsRepository.create(new Comment(null, "Old", postId));
        commentsRepository.update(new Comment(id, "New", postId));
        assertEquals("New", commentsRepository.getById(id).orElseThrow().content());
    }

    @Test
    @DisplayName("удалять комментарий по id")
    void delete_shouldRemoveComment() {
        long id = commentsRepository.create(new Comment(null, "ToDelete", postId));
        commentsRepository.delete(id);
        assertTrue(commentsRepository.getById(id).isEmpty());
    }

    @Test
    @DisplayName("возвращать количество комментариев поста")
    void countByPostId_shouldReturnCorrectCount() {
        commentsRepository.create(new Comment(null, "A", postId));
        commentsRepository.create(new Comment(null, "B", postId));
        assertEquals(2, commentsRepository.countByPostId(postId));
    }
}
