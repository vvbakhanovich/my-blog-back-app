package dev.vvbakh.tags.repository;


import java.util.List;

public interface TagRepository {
    void saveAll(long postId, List<String> tags);

    List<String> getAllByPostId(long postId);

    void updateAll(long postId, List<String> tags);
}
