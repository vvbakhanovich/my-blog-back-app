package dev.vvbakh.files;


import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void saveImage(long postId, MultipartFile data);

    byte[] getImage(long postId);
}
