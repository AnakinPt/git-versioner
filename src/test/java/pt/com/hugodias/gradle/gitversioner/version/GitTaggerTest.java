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
class GitTaggerTest {

  private File projectDir = new File("build/tmp/integrationTest/local");
  private File remoteDir = new File("build/tmp/integrationTest/remote");
  private File remoteTagDir = new File("build/tmp/integrationTest/remoteTag");

  private Git localGit;
  private Git remoteGit;
  private Git remoteTagGit;

  @BeforeEach
  public void setUp() throws IOException, GitAPIException, URISyntaxException {
    localGit = createRepository(projectDir);
    remoteGit = createRepository(remoteDir);
    remoteTagGit = createRepository(remoteTagDir);
    addCommitToLocalRepository(localGit);
    addRemoteAsLocalOrigin(localGit, "origin", remoteDir);
    addRemoteAsLocalOrigin(localGit, "tag", remoteTagDir);
  }

  @AfterEach
  public void tearDown() throws IOException {
    localGit.close();
    remoteGit.close();
    remoteTagGit.close();
    FileUtils.deleteDirectory(projectDir);
    FileUtils.deleteDirectory(remoteDir);
    FileUtils.deleteDirectory(remoteTagDir);
  }

  private Git createRepository(File file) throws IOException, GitAPIException {
    file.mkdirs();
    Git.init().setDirectory(file).call();
    return Git.open(new File(file.getAbsolutePath() + "/.git"));
  }

  private void addCommitToLocalRepository(Git git) throws GitAPIException {
    git.commit().setSign(false).setAllowEmpty(true).setMessage("Commit\nCommit").call();
  }

  private void addRemoteAsLocalOrigin(Git git, String remoteName, File remoteDir)
      throws URISyntaxException, GitAPIException {
    git.remoteAdd().setName(remoteName).setUri(new URIish(remoteDir.getAbsolutePath())).call();
  }

  @Test
  @DisplayName("Creates tag locally and pushes to remote repository")
  void createTagLocallyAndPushToRemote() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder().gitFolder(projectDir).config(TaggerConfig.builder().build()).build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getTagName()).isEqualTo("v1.0.0");
    assertThat(lastTag(remoteGit).getTagName()).isEqualTo("v1.0.0");
  }

  @Test
  @DisplayName("Creates overridden tag locally and pushes to remote repository")
  void createsOverridenTagLocallyAndPushesToRemote() throws GitAPIException, IOException {
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
  void createsTagWithNoMessageWhenNotSpecified() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder().gitFolder(projectDir).config(TaggerConfig.builder().build()).build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getFullMessage()).isEmpty();
    assertThat(lastTag(remoteGit).getFullMessage()).isEmpty();
  }

  @Test
  @DisplayName("Creates tag with message as last commit message")
  void createsTagWithMessageAsLastCommitMessage() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder()
            .gitFolder(projectDir)
            .config(TaggerConfig.builder().useCommitMessage(true).build())
            .build();
    tagger.tag("1.0.0");
    assertThat(lastTag(localGit).getFullMessage()).isEqualTo("Commit\nCommit");
    assertThat(lastTag(remoteGit).getFullMessage()).isEqualTo("Commit\nCommit");
  }

  @Test
  @DisplayName("Creates tag locally and pushes to remote repository")
  void createTagLocallyAndPushToNonStandardRemote() throws GitAPIException, IOException {
    GitTagger tagger =
        GitTagger.builder()
            .gitFolder(projectDir)
            .config(TaggerConfig.builder().remote("tag").build())
            .build();
    tagger.tag("2.0.0");
    assertThat(lastTag(localGit).getTagName()).isEqualTo("v2.0.0");
    assertThat(lastTag(remoteTagGit).getTagName()).isEqualTo("v2.0.0");
  }

  private RevTag lastTag(Git git) throws GitAPIException, IOException {
    RevWalk revWalk = new RevWalk(git.getRepository());
    Ref ref = git.tagList().call().get(0);
    return revWalk.parseTag(ref.getObjectId());
  }
}
