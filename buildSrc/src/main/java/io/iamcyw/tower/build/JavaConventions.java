package io.iamcyw.tower.build;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

class JavaConventions {
    void apply(Project project) {
        project.getPlugins()
                .withType(JavaBasePlugin.class, (java) -> {
                    project.setProperty("sourceCompatibility", JavaVersion.VERSION_11.getMajorVersion());
                    configureNameConventions(project);
                    configureJavaCompileConventions(project);
                    configureJavadocConventions(project);
                    configureTestConventions(project);
                    configureJarManifestConventions(project);
                    configureDependencyConventions(project);
                });
    }

    private void configureNameConventions(Project project) {
        project.getTasks()
                .withType(Jar.class, (jar) -> project.beforeEvaluate((evaluated) -> {
                    jar.getArchiveFileName()
                            .get();
                }));
    }

    private void configureDependencyConventions(Project project) {
        if (project.getProperties()
                .get("tower-dependencies") != null) {
            project.getPlugins()
                    .withType(JavaLibraryPlugin.class, (javaLibraryPlugin) -> project.getDependencies()
                            .add("api", project.getDependencies()
                                    .platform("io.iamcyw.tower:tower-dependencies:1.0.0-SNAPSHOT")));
        }
    }

    private void configureJarManifestConventions(Project project) {
        SourceSetContainer sourceSets = project.getExtensions()
                .getByType(SourceSetContainer.class);
        Set<String> sourceJarTaskNames = sourceSets.stream()
                .map(SourceSet::getSourcesJarTaskName)
                .collect(Collectors.toSet());
        Set<String> javadocJarTaskNames = sourceSets.stream()
                .map(SourceSet::getJavadocJarTaskName)
                .collect(Collectors.toSet());
        project.getTasks()
                .withType(Jar.class, (jar) -> project.afterEvaluate((evaluated) -> {
                    jar.manifest((manifest) -> {
                        Map<String, Object> attributes = new TreeMap<>();
                        attributes.put("Automatic-Module-Name", project.getName()
                                .replace("-", "."));
                        attributes.put("Build-Jdk-Spec", project.property("sourceCompatibility"));
                        attributes.put("Built-By", "tower");
                        attributes.put("Implementation-Title",
                                       determineImplementationTitle(project, sourceJarTaskNames, javadocJarTaskNames,
                                                                    jar));
                        attributes.put("Implementation-Version", project.getVersion());
                        manifest.attributes(attributes);
                    });
                }));
    }

    private String determineImplementationTitle(Project project, Set<String> sourceJarTaskNames, Set<String> javadocJarTaskNames, Jar jar) {
        if (sourceJarTaskNames.contains(jar.getName())) {
            return "Source for " + project.getName();
        }
        if (javadocJarTaskNames.contains(jar.getName())) {
            return "Javadoc for " + project.getName();
        }
        return project.getDescription();
    }

    private void configureTestConventions(Project project) {
        project.getTasks()
                .withType(Test.class, (test) -> {
                    test.useJUnitPlatform();
                    test.setMaxHeapSize("1024M");
                });
        project.getPlugins()
                .withType(JavaPlugin.class, (javaPlugin) -> {
                    project.getDependencies()
                            .add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter");
//                    project.getDependencies()
//                            .add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.mockito:mockito-core");
                    project.getDependencies()
                            .add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME,
                                 "org.junit.platform:junit-platform-launcher");
                });
    }

    private void configureJavadocConventions(Project project) {
        project.getTasks()
                .withType(Javadoc.class, (javadoc) -> {
                    ((StandardJavadocDocletOptions) javadoc.getOptions()).addStringOption("Xdoclint:none", "-quiet");
                    javadoc.getOptions()
                            .source(JavaVersion.VERSION_11.getMajorVersion())
                            .encoding("UTF-8");
                });
    }

    private void configureJavaCompileConventions(Project project) {
        project.getTasks()
                .withType(JavaCompile.class, (compile) -> {
                    compile.getOptions()
                            .setEncoding("UTF-8");
                    compile.setSourceCompatibility(JavaVersion.VERSION_11.getMajorVersion());
                    compile.setTargetCompatibility(JavaVersion.VERSION_11.getMajorVersion());
                });
    }

}
