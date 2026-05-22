package dev.vvbakh.posts;

import dev.vvbakh.posts.mapper.PostMapper;
import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.model.Post;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public PostDto createPost(@RequestBody CreatePostDto createPostDto) {
        final Post newPost = postMapper.toModel(createPostDto);
        final Post createdPost = postService.create(newPost);
        return postMapper.toDto(createdPost);
    }
}
