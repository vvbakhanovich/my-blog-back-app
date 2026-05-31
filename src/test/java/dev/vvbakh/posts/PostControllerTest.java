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

import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
                .andExpect(jsonPath("$.errors").exists());
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("возвращать 400 при создании поста с пустым text")
    void createPost_shouldReturn400WhenTextIsBlank() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Title","text":"","tags":[]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("возвращать 400 при создании поста без tags")
    void createPost_shouldReturn400WhenTagsIsNull() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Title","text":"Content"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("инкрементировать лайки и возвращать обновлённое значение")
    void incrementLikes_shouldReturnUpdatedLikesCount() throws Exception {
        long id = postRepository.create(new Post(null, "Title", "Content", 0));

        mockMvc.perform(post("/api/posts/{id}/likes", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    @DisplayName("возвращать 404 при инкременте лайков несуществующего поста")
    void incrementLikes_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(post("/api/posts/{id}/likes", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("удалять пост и возвращать 200 по запросу DELETE /api/posts/{id}")
    void deletePost_shouldReturn200() throws Exception {
        long id = postRepository.create(new Post(null, "To Delete", "Content", 0));

        mockMvc.perform(delete("/api/posts/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("возвращать 404 при удалении несуществующего поста")
    void deletePost_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(delete("/api/posts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("возвращать 400 при обновлении поста с несовпадающим id")
    void updatePost_shouldReturn400WhenIdNotMatch() throws Exception {
        long id = postRepository.create(new Post(23L, "Title", "Content", 0));

        mockMvc.perform(put("/api/posts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":999,"title":"Title","text":"Content","tags":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("загружать картинку поста и возвращать 200")
    void uploadImage_shouldReturn200() throws Exception {
        long id = postRepository.create(new Post(null, "Title", "Content", 0));
        MockMultipartFile image = new MockMultipartFile("image", "img.jpg",
                MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/posts/{id}/image", id)
                        .file(image))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("возвращать байты картинки по запросу GET /api/posts/{id}/image")
    void getImage_shouldReturnBytes() throws Exception {
        long id = postRepository.create(new Post(null, "Title", "Content", 0));
        MockMultipartFile image = new MockMultipartFile("image", "img.jpg",
                MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/posts/{id}/image", id).file(image));

        mockMvc.perform(get("/api/posts/{id}/image", id))
                .andExpect(status().isOk())
                .andExpect(content().bytes("fake-image".getBytes()));
    }

    @Test
    @DisplayName("возвращать 400 при загрузке файла с неверным типом")
    void uploadImage_shouldReturn400WhenContentTypeIsNotImage() throws Exception {
        long id = postRepository.create(new Post(null, "Title", "Content", 0));
        MockMultipartFile file = new MockMultipartFile("image", "doc.pdf",
                MediaType.APPLICATION_PDF_VALUE, "fake-pdf".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/posts/{id}/image", id)
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("возвращать 404 при загрузке картинки несуществующего поста")
    void uploadImage_shouldReturn404WhenPostNotFound() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "img.jpg",
                MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/posts/{id}/image", Long.MAX_VALUE)
                        .file(image))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("возвращать 404 при получении картинки несуществующего поста")
    void getImage_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/image", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("добавлять комментарий к посту и возвращать DTO")
    void addComment_shouldReturnCommentDto() throws Exception {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));

        mockMvc.perform(post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Nice post!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("Nice post!"))
                .andExpect(jsonPath("$.postId").value(postId));
    }

    @Test
    @DisplayName("возвращать 400 при добавлении комментария с пустым text")
    void addComment_shouldReturn400WhenTextIsBlank() throws Exception {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));

        mockMvc.perform(post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("возвращать 404 при добавлении комментария к несуществующему посту")
    void addComment_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(post("/api/posts/{id}/comments", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Hello"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("возвращать список комментариев поста")
    void getComments_shouldReturnCommentList() throws Exception {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));
        tagRepository.saveAll(postId, List.of());

        mockMvc.perform(post("/api/posts/{id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text":"First"}
                        """));
        mockMvc.perform(post("/api/posts/{id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text":"Second"}
                        """));

        mockMvc.perform(get("/api/posts/{id}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("возвращать 404 при запросе комментариев несуществующего поста")
    void getComments_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{id}/comments", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("обновлять комментарий и возвращать обновлённый DTO")
    void updateComment_shouldReturnUpdatedDto() throws Exception {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));

        String response = mockMvc.perform(post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Old"}
                                """))
                .andReturn().getResponse().getContentAsString();

        long commentId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"New"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New"));
    }

    @Test
    @DisplayName("возвращать 404 при обновлении комментария несуществующего поста")
    void updateComment_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", Long.MAX_VALUE, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Text"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("удалять комментарий и возвращать 200")
    void deleteComment_shouldReturn200() throws Exception {
        long postId = postRepository.create(new Post(null, "Title", "Content", 0));

        String response = mockMvc.perform(post("/api/posts/{id}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"To delete"}
                                """))
                .andReturn().getResponse().getContentAsString();

        long commentId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}/comments", postId))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("возвращать 404 при удалении комментария несуществующего поста")
    void deleteComment_shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", Long.MAX_VALUE, 1L))
                .andExpect(status().isNotFound());
    }
}
