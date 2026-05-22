package dev.vvbakh.tags.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcTagRepository implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAll(long postId, List<String> tags) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO post_tags(post_id, tag) VALUES(?, ?)",
                tags.stream().distinct().map(tag -> new Object[]{postId, tag}).toList()
        );
    }

    @Override
    public List<String> getAllByPostId(long postId) {
        return jdbcTemplate.queryForList(
                "SELECT tag FROM post_tags WHERE post_id = ?",
                String.class, postId
        );
    }
}
