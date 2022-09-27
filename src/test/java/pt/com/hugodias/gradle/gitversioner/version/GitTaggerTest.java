/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.version;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.com.hugodias.gradle.gitversioner.IntegrationTest;
import pt.com.hugodias.gradle.gitversioner.core.tag.GitTagger;
import pt.com.hugodias.gradle.gitversioner.core.tag.TaggerConfig;

@IntegrationTest
public class GitTaggerTest {

  private File projectDir = new File("build/tmp/integrationTest/local");
  private File remoteDir = new File("build/tmp/integrationTest/remote");

  private Git localGit;
  private Git remoteGit;

  @BeforeEach
  public void setUp() throws IOException, GitAPIException, URISyntaxException {
    localGit = createRepository(projectDir);
    remoteGit = createRepository(remoteDir);
    addCommitToLocalRepository(localGit);
    addRemoteAsLocalOrigin(localGit);
  }

  @AfterEach
  public void tearDown() throws IOException {
    localGit.close();
    remoteGit.close();
    FileUtils.deleteDirectory(projectDir);
    FileUtils.deleteDirectory(remoteDir);
  }

  private Git createRepository(File file) throws IOException, GitAPIException {
    file.mkdirs();
    Git.init().setDirectory(file).call();
    return Git.open(new File(file.getAbsolutePath() + "/.git"));
  }

  private void addCommitToLocalRepository(Git git) throws GitAPIException {
    git.commit().setSign(false).setAllowEmpty(true).setMessage("Commit\nCommit").call();
  }

  private void addRemoteAsLocalOrigin(Git git) throws URISyntaxException, GitAPIException {
    git.remoteAdd().setName("origin").setUri(new URIish(remoteDir.getAbsolutePath())).call();
  }

  @Test
  @DisplayName("Creates tag locally and pushes to remote repository")
  public void createTagLocallyAndPushToRemote() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder().gitFolder(projectDir).config(TaggerConfig.builder().build()).build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getTagName()).isEqualTo("v1.0.0");
    assertThat(lastTag(remoteGit).getTagName()).isEqualTo("v1.0.0");
  }

  @Test
  @DisplayName("Creates overridden tag locally and pushes to remote repository")
  public void createsOverridenTagLocallyAndPushesToRemote() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder()
            .gitFolder(projectDir)
            .config(TaggerConfig.builder().prefix("x").build())
            .build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getTagName()).isEqualTo("x1.0.0");
    assertThat(lastTag(remoteGit).getTagName()).isEqualTo("x1.0.0");
  }

  @Test
  @DisplayName("Creates tag with no message when not specified")
  public void createsTagWithNoMessageWhenNotSpecified() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder().gitFolder(projectDir).config(TaggerConfig.builder().build()).build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getFullMessage()).isEqualTo("");
    assertThat(lastTag(remoteGit).getFullMessage()).isEqualTo("");
  }

  @Test
  @DisplayName("Creates tag with message as last commit message")
  public void createsTagWithMessageAsLastCommitMessage() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder()
            .gitFolder(projectDir)
            .config(TaggerConfig.builder().useCommitMessage(true).build())
            .build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getFullMessage()).isEqualTo("Commit\nCommit");
    assertThat(lastTag(remoteGit).getFullMessage()).isEqualTo("Commit\nCommit");
  }

  private RevTag lastTag(Git git) throws GitAPIException, IOException {
    RevWalk revWalk = new RevWalk(git.getRepository());
    Ref ref = git.tagList().call().get(0);
    return revWalk.parseTag(ref.getObjectId());
  }
}
