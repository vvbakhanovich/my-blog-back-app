package dev.vvbakh.posts;

import dev.vvbakh.exception.IdNotMatchException;
import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.dto.UpdatePostDto;
import dev.vvbakh.posts.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createPost(@Valid @RequestBody CreatePostDto createPostDto) {
        return postService.create(createPostDto);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable long postId) {
        return postService.getById(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @Valid @RequestBody UpdatePostDto updatedPost) {
        validateMatchingIds(postId, updatedPost);
        return postService.updatePost(postId, updatedPost);
    }

    private void validateMatchingIds(long postId, UpdatePostDto updatedPost) {
        if (postId != updatedPost.id()) {
            throw new IdNotMatchException(postId, updatedPost.id());
        }
    }
}
