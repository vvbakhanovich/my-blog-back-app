package dev.vvbakh.posts;

import dev.vvbakh.comments.dto.CommentDto;
import dev.vvbakh.comments.dto.CreateCommentDto;
import dev.vvbakh.comments.service.CommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
@Validated
public class PostCommentController {

    private final CommentsService commentsService;

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
}
