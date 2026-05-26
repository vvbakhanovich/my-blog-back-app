package dev.vvbakh.files;

import java.io.IOException;

public interface FileService {

    void saveImage(long postId, byte[] data) throws IOException;

    byte[] getImage(long postId) throws IOException;
}
