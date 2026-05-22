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
        for (String tag : tags) {
            jdbcTemplate.update(
                    "INSERT INTO post_tags(post_id, tag) VALUES(?, ?)",
                    postId, tag
            );
        }
    }
}
