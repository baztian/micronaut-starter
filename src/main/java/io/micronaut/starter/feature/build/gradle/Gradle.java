package io.micronaut.starter.feature.build.gradle;

import io.micronaut.starter.command.CommandContext;
import io.micronaut.starter.feature.BuildFeature;
import io.micronaut.starter.template.BinaryTemplate;
import io.micronaut.starter.feature.build.gradle.templates.*;
import io.micronaut.starter.feature.build.gitignore;
import io.micronaut.starter.template.RockerTemplate;

public class Gradle implements BuildFeature {

    private static final String WRAPPER_JAR = "gradle/wrapper/gradle-wrapper.jar";
    private static final String WRAPPER_PROPS = "gradle/wrapper/gradle-wrapper.properties";

    @Override
    public String getName() {
        return "gradle";
    }

    @Override
    public void apply(CommandContext commandContext) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        commandContext.addTemplate("gradleWrapperJar", new BinaryTemplate(WRAPPER_JAR, classLoader.getResource(WRAPPER_JAR)));
        commandContext.addTemplate("gradleWrapperProperties", new BinaryTemplate(WRAPPER_PROPS, classLoader.getResource(WRAPPER_PROPS)));
        commandContext.addTemplate("gradleWrapper", new BinaryTemplate("gradlew", classLoader.getResource("gradle/gradlew"), true));
        commandContext.addTemplate("gradleWrapperBat", new BinaryTemplate("gradlew.bat", classLoader.getResource("gradle/gradlew.bat"), true));

        commandContext.addTemplate("build", new RockerTemplate("build.gradle", buildGradle.template(
                commandContext.getLanguage(),
                commandContext.getTestFramework(),
                commandContext.getProject(),
                commandContext.getFeatures()
        )));
        commandContext.addTemplate("gitignore", new RockerTemplate(".gitignore", gitignore.template()));
        commandContext.addTemplate("projectProperties", new RockerTemplate("gradle.properties", gradleProperties.template(commandContext.getProjectProperties())));
        commandContext.addTemplate("gradleSettings", new RockerTemplate("settings.gradle", settingsGradle.template(commandContext.getProject())));
    }
}
