package xyz.hamandishe.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface ClassGenerator {
    FileUtils fileUtils = new FileUtils();

    void generate(String className);

    default void save(Path filepath, List<String> classContent) {
        try {
            Files.write(filepath, classContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
