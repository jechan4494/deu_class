package service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class FileServiceTest {

    @TempDir
    Path tempDir;
    private String testFilePath;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test.json").toString();
    }

    @Test
    @DisplayName("파일이 없을 때 새로 생성하고 빈 리스트를 반환해야 함")
    void readData_shouldCreateNewFileWhenNotExists() throws IOException {
        // When
        List<String> result = FileService.readData(testFilePath, String.class);

        // Then
        assertThat(new File(testFilePath)).exists();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("데이터를 정상적으로 읽고 쓸 수 있어야 함")
    void readWriteData_shouldWorkCorrectly() throws IOException {
        // Given
        List<String> originalData = List.of("test1", "test2", "test3");

        // When
        FileService.writeData(testFilePath, originalData);
        List<String> readData = FileService.readData(testFilePath, String.class);

        // Then
        assertThat(readData).containsExactlyElementsOf(originalData);
    }

    @Test
    @DisplayName("잘못된 파일 경로일 때 IOException 발생해야 함")
    void readData_shouldThrowIOExceptionForInvalidPath() {
        // Given
        String invalidPath = "/invalid/path/test.json";

        // When & Then
        assertThatThrownBy(() -> FileService.readData(invalidPath, String.class))
                .isInstanceOf(IOException.class);
    }
}