package xyz.hamandishe.utils;

import xyz.hamandishe.constants.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static final int MAX_WALK_DEPTH = 50;
    public static final String JAVA_FILE_EXTENSION = ".java";

    public static Path getProjectRoot(){
        return Paths.get(System.getProperty("user.dir"));
    }

    public static Path getSourceRoot() {
        return Paths.get("src%smain%sjava".formatted(File.separator, File.separator));
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



    /**
     * Create a new package if it does not exist in the base package root
     * @param packageName The name of the package to be created
     * */
    public Path createPackage(String packageName) throws IOException {
        Path basePackagePath = getSourceRoot();
        Path packagePath = basePackagePath.resolve(packageName);
        Files.createDirectories(packagePath);
        return packagePath;
    }

    public String getBasePackageName() throws IOException{
        Path sourceRoot = getSourceRoot();

        try(var pathStream = Files.walk(sourceRoot)) {
            var packageRoot = pathStream.filter(Files::isDirectory)
                    .filter(this::isEmptyDir)
                    .findFirst().orElseThrow(IOException::new);

            return sourceRoot.relativize(packageRoot).toString();
        }
    }

    public boolean isEmptyDir(Path path){
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return entries.findFirst().isEmpty();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
