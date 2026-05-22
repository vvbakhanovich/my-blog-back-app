package dev.vvbakh.posts.mapper;

import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.dto.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    @Mapping(source = "content", target = "text")
    PostDto toDto(Post post);

    @Mapping(source = "text", target = "content")
    Post toModel(CreatePostDto createPostDto);
}
