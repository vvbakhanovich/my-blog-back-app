package dev.vvbakh.posts.mapper;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(source = "post.content", target = "text")
    @Mapping(source = "tags", target = "tags")
    @Mapping(target = "commentsCount", constant = "0L") // TODO: заменить на реальный счётчик после реализации комментариев
    PostDto toDto(Post post, List<String> tags);

    @Mapping(source = "text", target = "content")
    Post toModel(CreatePostDto dto); // id → null (Long), likesCount → 0 (long) — MapStruct по умолчанию
}
