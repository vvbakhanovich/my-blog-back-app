package dev.vvbakh.posts;

import dev.vvbakh.WebConfiguration;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.repository.PostRepository;
import dev.vvbakh.tags.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("PostController должен")
class  PostControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @DisplayName("возвращать страницу постов по запросу GET /api/posts")
    void getAllPosts_shouldReturnPostsPage() throws Exception {
        long id = postRepository.create(new Post(null, "Spring Tutorial", "Learn Spring", 0));
        tagRepository.saveAll(id, List.of("spring"));

        mockMvc.perform(get("/api/posts")
                        .param("search", "Spring")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].id").value(id))
                .andExpect(jsonPath("$.posts[0].title").value("Spring Tutorial"))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.lastPage").isNumber());
    }

    @Test
    @DisplayName("возвращать все посты при пустой строке поиска")
    void getAllPosts_shouldReturnAllPostsWhenSearchIsEmpty() throws Exception {
        postRepository.create(new Post(null, "Java Post", "Java content", 0));
        postRepository.create(new Post(null, "Spring Post", "Spring content", 0));

        mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(2));
    }

    @Test
    @DisplayName("фильтровать посты по поисковой строке")
    void getAllPosts_shouldFilterBySearch() throws Exception {
        postRepository.create(new Post(null, "Java Post", "Content about Java", 0));
        postRepository.create(new Post(null, "Spring Post", "Content about Spring", 0));

        mockMvc.perform(get("/api/posts")
                        .param("search", "Java")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("Java Post"));
    }

    @Test
    @DisplayName("обрезать текст поста до 128 символов в списке")
    void getAllPosts_shouldTruncateTextTo128Chars() throws Exception {
        String longContent = "A".repeat(200);
        postRepository.create(new Post(null, "Long Post", longContent, 0));

        mockMvc.perform(get("/api/posts")
                        .param("search", "Long")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].text").value("A".repeat(128) + "…"));
    }

    @Test
    @DisplayName("возвращать hasPrev=false и hasNext=true на первой странице из нескольких")
    void getAllPosts_shouldReturnCorrectPaginationFlags() throws Exception {
        for (int i = 1; i <= 3; i++) {
            postRepository.create(new Post(null, "Post " + i, "Content " + i, 0));
        }

        mockMvc.perform(get("/api/posts")
                        .param("search", "Post")
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.lastPage").value(2));
    }

    @Test
    @DisplayName("возвращать 400 при pageNumber меньше 1")
    void getAllPosts_shouldReturn400WhenPageNumberIsInvalid() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("создавать пост и возвращать DTO по запросу POST /api/posts")
    void createPost_shouldReturnCreatedPostDto() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Test Post","text":"Some content","tags":["java","spring"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.text").value("Some content"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    @DisplayName("возвращать пост с тегами по запросу GET /api/posts/{id}")
    void getPostById_shouldReturnPostDto() throws Exception {
        long id = postRepository.create(new Post(null, "My Post", "My Content", 0));
        tagRepository.saveAll(id, List.of("tag1", "tag2"));

        mockMvc.perform(get("/api/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("My Post"))
                .andExpect(jsonPath("$.text").value("My Content"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.commentsCount").value(0));
    }

    @Test
    @DisplayName("возвращать 404 по запросу GET /api/posts/{id} если пост не существует")
    void getPostById_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("обновлять пост и возвращать обновлённый DTO по запросу PUT /api/posts/{id}")
    void updatePost_shouldReturnUpdatedPostDto() throws Exception {
        long id = postRepository.create(new Post(null, "Old Title", "Old Content", 0));
        tagRepository.saveAll(id, List.of("oldTag"));

        mockMvc.perform(put("/api/posts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"title":"New Title","text":"New Content","tags":["newTag"]}
                                """.formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.text").value("New Content"))
                .andExpect(jsonPath("$.tags[0]").value("newTag"));
    }

    @Test
    @DisplayName("возвращать 404 при обновлении несуществующего поста")
    void updatePost_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(put("/api/posts/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"title":"Title","text":"Content","tags":[]}
                                """.formatted(Long.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("возвращать 400 при создании поста с пустым title")
    void createPost_shouldReturn400WhenTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","text":"Content","tags":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    @DisplayName("возвращать 400 при создании поста с пустым text")
    void createPost_shouldReturn400WhenTextIsBlank() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Title","text":"","tags":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.text").exists());
    }

    @Test
    @DisplayName("возвращать 400 при создании поста без tags")
    void createPost_shouldReturn400WhenTagsIsNull() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Title","text":"Content"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tags").exists());
    }

    @Test
    @DisplayName("возвращать 400 при обновлении поста с несовпадающим id")
    void updatePost_shouldReturn400WhenIdNotMatch() throws Exception {
        long id = postRepository.create(new Post(null, "Title", "Content", 0));

        mockMvc.perform(put("/api/posts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":999,"title":"Title","text":"Content","tags":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
