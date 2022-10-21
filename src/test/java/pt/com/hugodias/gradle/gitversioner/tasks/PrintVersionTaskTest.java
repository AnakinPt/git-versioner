/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.*;
import pt.com.hugodias.gradle.gitversioner.util.Gradle;
import pt.com.hugodias.gradle.gitversioner.util.Project;

class PrintVersionTaskTest {

  private File directory;
  private Project project;
  private Gradle gradle;

  @BeforeEach
  public void setUp() {
    directory = new File("build/tmp/functionalTest/PrintVersionTaskTest/");
    directory.mkdirs();
    project = Project.builder().directory(directory).build();
    gradle = Gradle.builder().directory(directory).build();
  }

  @AfterEach
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(directory);
  }

  @DisplayName("prints version using default configuration when none is supplied in Groovy")
  @Test
  void testPrintsVersionUsingDefaultConfigurationGroovy() throws IOException, GitAPIException {
    project.withSettingsFile().withGit().withGroovyGradleFile("default");
    addCommits();

    BuildResult result = gradle.runTask("printVersion");

    assertThat(result.getOutput()).contains("1.1.1.1");
  }

  @DisplayName("prints version using provided configuration when supplied in Groovy")
  @Test
  void testPrintsVersionUsingDefinedConfigurationGroovy() throws IOException, GitAPIException {
    project.withSettingsFile().withGit().withGroovyGradleFile("configured");
    addCommits();

    BuildResult result = gradle.runTask("printVersion");

    assertThat(result.getOutput()).contains("2.1.1-4");
  }

  @DisplayName(
      "prints version using default configuration when none is supplied in Groovy and we have an empty repository")
  @Test
  void testPrintsVersionUsingDefaultConfigurationGroovyEmptyRepository()
      throws IOException, GitAPIException {
    project.withSettingsFile().withGit().withGroovyGradleFile("default");

    BuildResult result = gradle.runTask("printVersion");

    assertThat(result.getOutput()).contains("0.0.0");
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
