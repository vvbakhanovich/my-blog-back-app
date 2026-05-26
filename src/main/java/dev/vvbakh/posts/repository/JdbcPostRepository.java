package dev.vvbakh.posts.repository;

import dev.vvbakh.posts.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JdbcPostRepository implements PostRepository {

    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rn) -> new Post(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getLong("likes_count")
    );

    private final JdbcTemplate jdbcTemplate;

    @Override
    public long create(Post newPost) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(
                    "INSERT INTO posts(title, content) VALUES(?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, newPost.title());
            ps.setString(2, newPost.content());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Post> getById(long id) {
        return jdbcTemplate.query(
                "SELECT id, title, content, likes_count FROM posts WHERE id = ?",
                POST_ROW_MAPPER,
                id
        ).stream().findFirst();
    }

    @Override
    public void update(Post updated) {
        jdbcTemplate.update("UPDATE posts SET title = ?, content = ? WHERE id = ?", updated.title(), updated.content(), updated.id());
    }

    @Override
    public List<Post> getAll(String search, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        String pattern = "%" + search.toLowerCase() + "%";
        return jdbcTemplate.query(
                "SELECT id, title, content, likes_count FROM posts " +
                "WHERE LOWER(title) LIKE ? OR LOWER(content) LIKE ? LIMIT ? OFFSET ?",
                POST_ROW_MAPPER, pattern, pattern, pageSize, offset
        );
    }

    @Override
    public long countAll(String search) {
        String pattern = "%" + search.toLowerCase() + "%";
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE LOWER(title) LIKE ? OR LOWER(content) LIKE ?",
                Long.class, pattern, pattern
        );
    }

    @Override
    public void delete(long postId) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", postId);
    }

    @Override
    public long incrementLikes(long postId) {
        jdbcTemplate.update("UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?", postId);
        return jdbcTemplate.queryForObject("SELECT likes_count FROM posts WHERE id = ?", Long.class, postId);
    }
}
