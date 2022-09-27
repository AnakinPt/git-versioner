/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.*;
import pt.com.hugodias.gradle.gitversioner.util.Gradle;
import pt.com.hugodias.gradle.gitversioner.util.Project;

public class TagVersionTaskTest {

  private File directory;
  private File remoteDir;
  private Project project;
  private Gradle gradle;
  private Git localGit;
  private Git remoteGit;

  @BeforeEach
  public void setUp() throws GitAPIException, URISyntaxException {
    directory = new File("build/tmp/functionalTest/TagVersionTaskTest/local/");
    directory.mkdirs();
    remoteDir = new File("build/tmp/functionalTest/TagVersionTaskTest/remote/");
    remoteDir.mkdirs();

    localGit = createRepository(directory);
    remoteGit = createRepository(remoteDir);
    addRemote(localGit);
    addCommit(localGit);
    project = Project.builder().directory(directory).build();
    gradle = Gradle.builder().directory(directory).build();
  }

  @AfterEach
  public void tearDown() throws IOException {
    localGit.close();
    remoteGit.close();
    FileUtils.deleteDirectory(directory);
    FileUtils.deleteDirectory(remoteDir);
  }

  @DisplayName(
      "Creates tag locally and pushes to remote repository using default configuration when none is supplied in Groovy")
  @Test
  public void testCreateTagLocallyAndPushToRemoteUsingDefaultConfigurationInGroovy()
      throws IOException, GitAPIException {
    project.withSettingsFile().withGroovyGradleFile("default");

    gradle.runTask("tagVersion");

    assertThat(lastTag(localGit).getTagName()).isEqualTo("v1.0.0");
    assertThat(lastTag(remoteGit).getTagName()).isEqualTo("v1.0.0");
  }

  @DisplayName(
      "Creates tag locally and pushes to remote repository using provided configuration when supplied in Kotlin")
  @Test
  @Disabled
  public void testCreateTagLocallyAndPushToRemoteUsingProvidedConfigurationInKotlin()
      throws IOException, GitAPIException {
    project.withSettingsFile().withKotlinGradleFile("configured");

    gradle.runTask("tagVersion");

    assertThat(lastTag(localGit).getTagName()).isEqualTo("x1.1.1-1");
    assertThat(lastTag(remoteGit).getTagName()).isEqualTo("x1.1.1-1");
  }

  private RevTag lastTag(Git git) throws GitAPIException, IOException {
    RevWalk revWalk = new RevWalk(git.getRepository());
    Ref ref = git.tagList().call().get(0);
    assertNotNull(ref);
    return revWalk.parseTag(ref.getObjectId());
  }

  private Git createRepository(File directory) throws GitAPIException {
    return Git.init().setDirectory(directory).call();
  }

  private void addRemote(Git git) throws URISyntaxException, GitAPIException {
    git.remoteAdd().setName("origin").setUri(new URIish(remoteDir.getAbsolutePath())).call();
  }

  private void addCommit(Git git) throws GitAPIException {
    git.commit().setSign(false).setAllowEmpty(true).setMessage("[major]").call();
  }
}
