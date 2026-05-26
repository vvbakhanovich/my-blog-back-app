package dev.vvbakh.files;

import dev.vvbakh.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DiskFilesService implements FileService {

    private static final String IMAGE_NAME = "image";
    private final Path baseDir;

    public DiskFilesService(@Value("${files.images.dir}") String baseDir) {
        this.baseDir = Path.of(baseDir);
    }

    @Override
    public void saveImage(long postId, byte[] data) throws IOException {
        final Path pathToSave = baseDir.resolve(String.valueOf(postId));
        Files.createDirectories(pathToSave);
        Files.write(pathToSave.resolve(IMAGE_NAME), data);
    }

    @Override
    public byte[] getImage(long postId) throws IOException {
        Path file = baseDir.resolve(String.valueOf(postId)).resolve(IMAGE_NAME);
        if (!Files.exists(file)) throw new NotFoundException("Картинка поста с id '" + postId + "' не найдена");
        return Files.readAllBytes(file);
    }
}
