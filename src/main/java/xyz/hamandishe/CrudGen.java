package xyz.hamandishe;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import xyz.hamandishe.utils.ControllerClassGenerator;
import xyz.hamandishe.utils.FileUtils;

import java.io.File;
import java.util.List;


@Mojo(name = "generate", defaultPhase = LifecyclePhase.INITIALIZE)
public class CrudGen extends AbstractMojo {
    private final FileUtils fileUtils = new FileUtils(getLog());
    private ControllerClassGenerator controllerClassGenerator = new ControllerClassGenerator(getLog());

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
        getLog().info("Processing file: ".concat(file.getAbsolutePath()));
        controllerClassGenerator.generate(file);
    }
}