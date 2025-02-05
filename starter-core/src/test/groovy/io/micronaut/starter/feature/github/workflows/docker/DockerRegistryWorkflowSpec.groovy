package io.micronaut.starter.feature.github.workflows.docker

import io.micronaut.starter.BeanContextSpec
import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.*
import spock.lang.Unroll

class DockerRegistryWorkflowSpec extends BeanContextSpec implements CommandOutputFixture{

    void 'test github workflow readme'(){
        when:
        def output = generate([DockerRegistryWorkflow.NAME])
        def readme = output['README.md']

        then:
        readme
        readme.contains("""
Add the following GitHub secrets:

| Name | Description |
| ---- | ----------- |
| DOCKER_USERNAME | Username for Docker registry authentication. |
| DOCKER_PASSWORD | Docker registry password. |
| DOCKER_REPOSITORY_PATH | Path to the docker image repository inside the registry, e.g. for the image `foo/bar/micronaut:0.1` it is `foo/bar`. |
| DOCKER_REGISTRY_URL | Docker registry url. |
""")
    }

    @Unroll
    void 'test github workflow is created for #buildTool'(BuildTool buildTool, String workflowName) {
        when:
        def output = generate(ApplicationType.DEFAULT,
                new Options(Language.JAVA, TestFramework.JUNIT, buildTool, JdkVersion.JDK_11),
                [DockerRegistryWorkflow.NAME])
        def workflow = output[".github/workflows/${workflowName}"]

        then:
        workflow
        workflow.contains("name: Java CI")

        where:
        buildTool | workflowName
        BuildTool.GRADLE | "gradle.yml"
        BuildTool.MAVEN | "maven.yml"
    }

    void 'test push to docker workflow for maven'() {
        when:
        def output = generate(ApplicationType.DEFAULT,
                new Options(Language.JAVA, TestFramework.JUNIT, BuildTool.MAVEN, JdkVersion.JDK_11),
                [DockerRegistryWorkflow.NAME])
        def maven = output['.github/workflows/maven.yml']

        then:
        maven
        maven.contains("DOCKER_IMAGE=`echo \"\${DOCKER_REGISTRY_URL}/\${DOCKER_REPOSITORY_PATH}/foo")
    }

    void 'test docker image is configured in build.gradle'() {
        when:
        def output = generate([DockerRegistryWorkflow.NAME])
        def gradle = output['build.gradle']

        then:
        gradle
        gradle.contains("""
dockerBuild {
    images = [\"\${System.env.DOCKER_IMAGE ?: project.name}:\$project.version"]
}""")
    }

    @Unroll
    void 'test github gradle workflow java version for #version'(JdkVersion version){
        when:
        def output = generate(ApplicationType.DEFAULT,
                new Options(Language.JAVA, TestFramework.JUNIT, BuildTool.GRADLE, version),
                [DockerRegistryWorkflow.NAME])
        def workflow = output['.github/workflows/gradle.yml']

        then:
        workflow
        workflow.contains("java-version: ${version.majorVersion()}")

        where:
        version << JdkVersion.values()
    }
}
