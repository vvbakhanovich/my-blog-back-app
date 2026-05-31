package dev.vvbakh.files;

import dev.vvbakh.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiskFilesService должен")
class DiskFilesServiceTest {

    @TempDir
    Path tempDir;

    private DiskFilesService filesService;

    @BeforeEach
    void setup() {
        filesService = new DiskFilesService(tempDir.toString());
    }

    @Test
    @DisplayName("сохранять картинку на диск и возвращать её содержимое")
    void saveAndGetImage_shouldReturnSavedBytes() {
        byte[] data = "test image".getBytes();
        filesService.saveImage(1L, new MockMultipartFile("image", data));
        assertArrayEquals(data, filesService.getImage(1L));
    }

    @Test
    @DisplayName("создавать файл по пути {baseDir}/{postId}/image")
    void saveImage_shouldCreateFileAtExpectedPath() {
        filesService.saveImage(42L, new MockMultipartFile("image", "img".getBytes()));
        Path expected = tempDir.resolve("42").resolve("image");
        assertTrue(expected.toFile().exists());
    }

    @Test
    @DisplayName("бросать NotFoundException если картинка не существует")
    void getImage_shouldThrowNotFoundExceptionWhenFileAbsent() {
        assertThrows(NotFoundException.class, () -> filesService.getImage(999L));
    }
}
