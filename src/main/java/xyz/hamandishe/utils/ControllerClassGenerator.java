package xyz.hamandishe.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.logging.Log;
import org.burningwave.core.classes.*;
import xyz.hamandishe.constants.FileType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ControllerClassGenerator implements ClassGenerator{
    private static final String PACKAGE_NAME = "controllers";
    private String className;
    private List<String> classContent = new ArrayList<>();
    private Log log;
    private FileUtils fileUtils;

    public ControllerClassGenerator(Log log) {
        this.log = log;
        fileUtils = new FileUtils(log);
    }

    @Override
    public void generate(File file) {
        var root = FileUtils.getProjectRoot();
        var javaRoot = FileUtils.getSourceRoot();

        String classFilename = FilenameUtils.getBaseName(file.getAbsolutePath()).toLowerCase();
        this.className = AppUtils.capitalize(
                classFilename.contains("entity") ? classFilename.replace("entity", "Controller") : classFilename.concat("Controller")
        );
        log.info("Class filename: ".concat(className));

        try {
            this.classContent = Files.readAllLines(file.toPath());

            String projectPackageName = fileUtils.getProjectPackageName(root);
            log.info("Project Package: %s".formatted(projectPackageName));

            UnitSourceGenerator unitSG = UnitSourceGenerator.create(FileUtils.joinPackages(projectPackageName, PACKAGE_NAME)).addImport(
                    "org.springframework.http.ResponseEntity",
                    "org.springframework.http.HttpStatus",
                    "org.springframework.web.bind.annotation.*"
            ).addClass(
                    ClassSourceGenerator.create(
                            TypeDeclarationSourceGenerator.create(className)
                    ).addModifier(
                            Modifier.PUBLIC
                    ).addAnnotation(AnnotationSourceGenerator.create(FileType.RestController.name())
                    ).addMethod(
                            FunctionSourceGenerator.create("listItems")
                                    .setReturnType(
                                            TypeDeclarationSourceGenerator.create("ResponseEntity")
                                                    .addGeneric(GenericSourceGenerator.create("?"))
                                    ).addParameter(VariableSourceGenerator.create("Object request"))
                                    .addModifier(Modifier.PUBLIC)
                                    .addAnnotation(AnnotationSourceGenerator.create("GetMapping"))
                                    .addBodyCodeLine(" return new ResponseEntity<>(\"Data\", HttpStatus.OK);")
                    )
            );
            Path newPackageFullPath = root.resolve(javaRoot).resolve(FileUtils.packageToPath(projectPackageName));
            Path packagePath = fileUtils.createPackage(PACKAGE_NAME, newPackageFullPath);
            Path filepath = packagePath.resolve(Paths.get("%s%s".formatted(className, FileUtils.JAVA_FILE_EXTENSION)));

            //save
            save(filepath, unitSG.make());
        } catch (IOException e) {
            log.error("Failed to generate controller from file: ".concat(file.getAbsolutePath()));
            throw new RuntimeException(e);
        }
    }
}
