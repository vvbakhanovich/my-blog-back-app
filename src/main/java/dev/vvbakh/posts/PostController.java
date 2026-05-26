package dev.vvbakh.posts;

import dev.vvbakh.exception.IdNotMatchException;
import dev.vvbakh.posts.dto.CreatePostDto;
import dev.vvbakh.posts.dto.PostDto;
import dev.vvbakh.posts.dto.PostsPageDto;
import dev.vvbakh.posts.dto.UpdatePostDto;
import dev.vvbakh.posts.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @GetMapping
    public PostsPageDto getAllPosts(@RequestParam String search,
                                    @RequestParam(defaultValue = "1") @Min(1) int pageNumber,
                                    @RequestParam(defaultValue = "10") @Min(0) int pageSize) {
        return postService.getAll(search, pageNumber, pageSize);
    }

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

    @PostMapping("/{postId}/likes")
    public long incrementLikes(@PathVariable long postId) {
        return postService.incrementLikes(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
    }

    @PutMapping("/{postId}/image")
    public void uploadImage(@PathVariable long postId,
                            @RequestParam("image") MultipartFile image) {
        postService.uploadImage(postId, image);
    }

    @GetMapping(value = "/{postId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable long postId) {
        return postService.getImage(postId);
    }


    private void validateMatchingIds(long postId, UpdatePostDto updatedPost) {
        if (postId != updatedPost.id()) {
            throw new IdNotMatchException(postId, updatedPost.id());
        }
    }
}
