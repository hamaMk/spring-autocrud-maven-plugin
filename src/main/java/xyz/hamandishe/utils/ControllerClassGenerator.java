package xyz.hamandishe.utils;

import org.burningwave.core.classes.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControllerClassGenerator implements ClassGenerator{
    private static final String PACKAGE_NAME = "controllers";
    private String className;
    private List<String> classContent = new ArrayList<>();

    @Override
    public void generate(JavaClass javaClass, File file) {
        String classFilename = javaClass.getClassFileName().toLowerCase();
        this.className = AppUtils.capitalize(
                classFilename.contains("entity") ? classFilename.replace("entity", "Controller") : classFilename.concat("Controller")
        );

        try {
            this.classContent = Files.readAllLines(file.toPath());

            Path packagePath = fileUtils.createPackage(PACKAGE_NAME);
            Path filepath = packagePath.resolve(Paths.get("%s%s".formatted(className, FileUtils.JAVA_FILE_EXTENSION)));

            UnitSourceGenerator unitSG = UnitSourceGenerator.create(fileUtils.getBasePackageName()).addClass(
                    ClassSourceGenerator.create(
                            TypeDeclarationSourceGenerator.create(className)
                    ).addModifier(
                            Modifier.PUBLIC
                    ).addMethod(
                            FunctionSourceGenerator.create("listItems")
                                    .setReturnType(
                                            TypeDeclarationSourceGenerator.create(Comparable.class)
                                                    .addGeneric(GenericSourceGenerator.create(Date.class))
                                    ).addParameter(VariableSourceGenerator.create(LocalDateTime.class, "localDateTime"))
                                    .addModifier(Modifier.PUBLIC)
                                    .addAnnotation(AnnotationSourceGenerator.create(Override.class))
                                    .addBodyCodeLine("return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());")
                                    .useType(ZoneId.class)
                    )
            );


            //save
            save(filepath, unitSG.make());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
