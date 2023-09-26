package xyz.hamandishe;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import xyz.hamandishe.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;


@Mojo(name = "generate", defaultPhase = LifecyclePhase.INITIALIZE)
public class CrudGen extends AbstractMojo {
    private FileUtils fileUtils = new FileUtils();

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
            }
            entities.forEach(file -> getLog().info(file.getAbsolutePath()));
        } catch (Exception e) {
            getLog().error("Failed to generate CRUDs: ", e);
        }
    }
}