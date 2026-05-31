package dev.vvbakh.posts;

import dev.vvbakh.comments.dto.CommentDto;
import dev.vvbakh.comments.dto.CreateCommentDto;
import dev.vvbakh.comments.service.CommentsService;
import dev.vvbakh.exception.IdNotMatchException;
import dev.vvbakh.exception.UploadFileException;
import dev.vvbakh.files.FileService;
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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;
    private final CommentsService commentsService;
    private final FileService fileService;

    @GetMapping
    public PostsPageDto getAllPosts(@RequestParam String search,
                                    @RequestParam(defaultValue = "1") @Min(1) int pageNumber,
                                    @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
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
        postService.getById(postId);
        fileService.saveImage(postId, image);
    }

    @GetMapping(value = "/{postId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable long postId) {
        postService.getById(postId);
        return fileService.getImage(postId);
    }

    @PostMapping("/{postId}/comments")
    public CommentDto addComment(@PathVariable long postId,
                                 @Valid @RequestBody CreateCommentDto dto) {
        return commentsService.add(postId, dto);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentDto> getComments(@PathVariable long postId) {
        return commentsService.getAll(postId);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable long postId,
                                    @PathVariable long commentId,
                                    @Valid @RequestBody CreateCommentDto dto) {
        return commentsService.update(postId, commentId, dto);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteComment(@PathVariable long postId,
                              @PathVariable long commentId) {
        commentsService.delete(postId, commentId);
    }

    private void validateMatchingIds(long postId, UpdatePostDto updatedPost) {
        if (postId != updatedPost.id()) {
            throw new IdNotMatchException(postId, updatedPost.id());
        }
    }
}
