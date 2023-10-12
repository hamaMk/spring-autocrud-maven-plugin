package xyz.hamandishe.utils;

import org.burningwave.core.classes.JavaClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface ClassGenerator {
    void generate(File file);

    default void save(Path filepath, String classContent) {
        try {
            Files.writeString(filepath, classContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
