package dev.vvbakh.posts;

import dev.vvbakh.exception.InvalidFileTypeException;
import dev.vvbakh.files.FileService;
import dev.vvbakh.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostImageController {

    private final PostService postService;
    private final FileService fileService;

    @PutMapping("/{postId}/image")
    public void uploadImage(@PathVariable long postId,
                            @RequestParam("image") MultipartFile image) {
        String contentType = image.getContentType();
        validateContentType(contentType);
        postService.getById(postId);
        fileService.saveImage(postId, image);
    }

    @GetMapping(value = "/{postId}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable long postId) {
        postService.getById(postId);
        return fileService.getImage(postId);
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException(contentType);
        }
    }
}
