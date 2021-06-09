package io.iamcyw.tower.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;

import java.util.Collections;

public class AutoConfigurationPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins()
                .apply(DeployedPlugin.class);

        project.getPlugins()
                .withType(JavaPlugin.class, (javaPlugin) -> {
                    Configuration annotationProcessors = project.getConfigurations()
                            .getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME);
                });
    }

}
