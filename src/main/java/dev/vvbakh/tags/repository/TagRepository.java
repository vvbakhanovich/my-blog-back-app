package dev.vvbakh.tags.repository;

import java.util.List;

public interface TagRepository {
    void saveAll(long createdPostId, List<String> tags);
}
