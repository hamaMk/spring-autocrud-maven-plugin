package xyz.hamandishe.utils;

import org.apache.maven.plugin.logging.Log;
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
    private static final String PACKAGE_KEYWORD = "package";
    private static final String TERMINATOR = ";";
    private final Log log;

    public FileUtils(Log log) {
        this.log = log;
    }

    public static String joinPackages(String... packageNames){
        return String.join(".", packageNames);
    }

    public static Path packageToPath(String packageName){
        return Path.of(packageName.replace(".", File.separator));
    }

    public static Path getProjectRoot(){
        return Paths.get(System.getProperty("user.dir"));
    }

    public static Path getSourceRoot() {
        return Paths.get("%s%ssrc%smain%sjava".formatted(getProjectRoot(), File.separator,File.separator, File.separator));
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
            log.error("Failed to check file type");
            throw new RuntimeException();
        }
    }



    /**
     * Create a new package if it does not exist in the base package root
     * @param packageName The name of the package to be created
     * */
    public Path createPackage(String packageName, Path rootPath) throws IOException {
        log.debug("Creating new package @: %s".formatted(rootPath));
        Path packagePath = rootPath.resolve(packageName);
        Files.createDirectories(packagePath);
        return packagePath;
    }

    public String getProjectPackageName(Path rootPath) {
        log.debug("Resolving project package name");
        try {
            List<File>  mainClass = list(rootPath, FileType.SpringBootApplication);
            System.out.println(mainClass);
            if(mainClass==null || mainClass.isEmpty()){
                log.error("Main class not found");
            }else {
                List<String> lines = Files.readAllLines(mainClass.get(0).toPath());
                String packageLine = lines.stream().filter(line->line.contains(PACKAGE_KEYWORD)).findFirst()
                        .orElseThrow();
                return packageLine.substring(
                        packageLine.indexOf(PACKAGE_KEYWORD)+PACKAGE_KEYWORD.length(), packageLine.indexOf(TERMINATOR))
                                .trim();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
