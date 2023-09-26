package xyz.hamandishe.utils;

import xyz.hamandishe.constants.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static final int MAX_WALK_DEPTH = 50;
    public static final String JAVA_FILE_EXTENSION = ".java";

    public static Path getProjectRoot(){
        return Paths.get(System.getProperty("user.dir"));
    }

    public List<File> listEntities(Path root) throws IOException {
        return list(root, FileType.Entity);
    }


    /**
     * List files of specified type contained in the root directory.
     * @param root - The root folder to search from.
     * @param fileType - FileType enum specifying what kind of file to read.
     */
    public List<File> list(Path root, FileType fileType) throws IOException {
        try (Stream<Path> stream = Files.walk(root, MAX_WALK_DEPTH)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().endsWith(JAVA_FILE_EXTENSION))
                    .filter(filePath -> isOfType(filePath, fileType))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    public boolean isOfType(Path filePath, FileType fileType){
        try {
            String data = Files.readString(filePath);
            return data.contains("@%s".formatted(fileType.name()));
        } catch (IOException e) {
            return true;
        }
    }
}
