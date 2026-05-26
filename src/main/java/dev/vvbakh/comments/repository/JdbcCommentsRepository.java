package dev.vvbakh.comments.repository;

import dev.vvbakh.comments.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcCommentsRepository implements CommentsRepository {

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rn) -> new Comment(
            rs.getLong("id"),
            rs.getString("content"),
            rs.getLong("post_id")
    );

    private final JdbcTemplate jdbcTemplate;

    @Override
    public long create(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(
                    "INSERT INTO post_comments(content, post_id) VALUES(?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, comment.content());
            ps.setLong(2, comment.postId());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Comment> getById(long commentId) {
        return jdbcTemplate.query(
                "SELECT id, content, post_id FROM post_comments WHERE id = ?",
                COMMENT_ROW_MAPPER, commentId
        ).stream().findFirst();
    }

    @Override
    public List<Comment> getAllByPostId(long postId) {
        return jdbcTemplate.query(
                "SELECT id, content, post_id FROM post_comments WHERE post_id = ?",
                COMMENT_ROW_MAPPER, postId
        );
    }

    @Override
    public void update(Comment comment) {
        jdbcTemplate.update("UPDATE post_comments SET content = ? WHERE id = ?",
                comment.content(), comment.id());
    }

    @Override
    public void delete(long commentId) {
        jdbcTemplate.update("DELETE FROM post_comments WHERE id = ?", commentId);
    }

    @Override
    public long countByPostId(long postId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_comments WHERE post_id = ?",
                Long.class, postId
        );
    }
}
