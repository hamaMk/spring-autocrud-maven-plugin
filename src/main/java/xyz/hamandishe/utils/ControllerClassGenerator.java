package xyz.hamandishe.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ControllerClassGenerator implements ClassGenerator{
    private static final String PACKAGE_NAME = "controllers";
    private String className;
    private List<String> classContent = new ArrayList<>();

    @Override
    public void generate(String className) {
        this.className = className;
    }

    @Override
    public void save() {
        try {
            Path packagePath = fileUtils.createPackage(PACKAGE_NAME);
            Path filepath = packagePath.resolve(Paths.get(className));
            Files.write(filepath, classContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
