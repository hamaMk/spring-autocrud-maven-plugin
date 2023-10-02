package xyz.hamandishe;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.burningwave.core.classes.JavaClass;
import org.burningwave.core.io.FileSystemItem;
import xyz.hamandishe.constants.FileType;
import xyz.hamandishe.utils.ControllerClassGenerator;
import xyz.hamandishe.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;


@Mojo(name = "generate", defaultPhase = LifecyclePhase.INITIALIZE)
public class CrudGen extends AbstractMojo {
    private final FileUtils fileUtils = new FileUtils();
    private ControllerClassGenerator controllerClassGenerator = new ControllerClassGenerator();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Spring boot crud generator is running");

        //list all entities in project
        try {
            var root = FileUtils.getProjectRoot();
            getLog().info("Root dir is: "+root);
            List<File> entities = fileUtils.listEntities(root);

            if(entities.isEmpty()){
                getLog().warn("No files found");
            }else {
                entities.forEach(this::generateClasses);
            }

        } catch (Exception e) {
            getLog().error("Failed to generate CRUDs: ", e);
        }
    }

    public void generateClasses(File file){
        FileSystemItem fSI = FileSystemItem.of(file);
        try(JavaClass javaClass = fSI.toJavaClass()) {
            controllerClassGenerator.generate(javaClass, file);
        }
    }
}