/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.*;
import pt.com.hugodias.gradle.gitversioner.util.Gradle;
import pt.com.hugodias.gradle.gitversioner.util.Project;

@FunctionalTest
class GitVersionerTest {

  private File directory;
  private Project project;
  private Gradle gradle;

  @BeforeEach
  void setUp() {
    directory = new File("build/tmp/functionalTest/GitVersionerTest/");
    directory.mkdirs();
    project = Project.builder().directory(directory).build();
    gradle = Gradle.builder().directory(directory).build();
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(directory);
  }

  @DisplayName("Version is available after forcing version resolution in Groovy")
  @Test
  void testVersionIsAvailableAfterForcingVersionGroovy() throws IOException, GitAPIException {
    project.withSettingsFile().withGit().withGroovyGradleFile("configured");
    addCommits();

    BuildResult result = gradle.runTask("printVersionEarly");

    assertThat(result.getOutput()).contains("2.1.1-4");
  }

  @DisplayName("Version is available after forcing version resolution in Kotlin")
  @Test
  void testVersionIsAvailableAfterForcingVersionKotlin() throws IOException, GitAPIException {
    project.withSettingsFile().withGit().withKotlinGradleFile("configured");
    addCommits();

    BuildResult result = gradle.runTask("printVersionEarly");

    assertThat(result.getOutput()).contains("2.1.1-4");
  }

  private void addCommits() throws GitAPIException {
    project
        .withCommit("trex")
        .withCommit("stego")
        .withCommit("compy")
        .withCommit("[major]")
        .withCommit("[minor]")
        .withCommit("[patch]")
        .withCommit("message");
  }
}
