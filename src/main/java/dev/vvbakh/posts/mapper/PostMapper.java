package dev.vvbakh.posts.mapper;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.dto.UpdatePostDto;
import dev.vvbakh.posts.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(source = "post.content", target = "text")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "commentsCount", target = "commentsCount")
    PostDto toDto(Post post, List<String> tags, long commentsCount);

    @Mapping(source = "text", target = "content")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    Post toModel(CreatePostDto dto);

    @Mapping(source = "text", target = "content")
    @Mapping(target = "likesCount", ignore = true)
    Post toModel(UpdatePostDto dto);
}
