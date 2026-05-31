package dev.vvbakh.files;

import dev.vvbakh.exception.NotFoundException;
import dev.vvbakh.exception.UploadFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DiskFilesService implements FileService {

    private static final String IMAGE_NAME = "image";
    private final Path baseDir;

    public DiskFilesService(@Value("${files.images.dir}") String baseDir) {
        this.baseDir = Path.of(baseDir);
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для изображений: " + baseDir, e);
        }
    }

    @Override
    public void saveImage(long postId, MultipartFile data) {
        final Path pathToSave = baseDir.resolve(String.valueOf(postId));
        try {
            Files.createDirectories(pathToSave);
            Files.write(pathToSave.resolve(IMAGE_NAME), data.getBytes());
        } catch (IOException e) {
            throw new UploadFileException(e);
        }
    }

    @Override
    public byte[] getImage(long postId) {
        Path file = baseDir.resolve(String.valueOf(postId)).resolve(IMAGE_NAME);
        if (!Files.exists(file)) throw new NotFoundException("Картинка поста с id '" + postId + "' не найдена");
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UploadFileException(e);
        }
    }
}
