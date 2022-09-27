/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.com.hugodias.gradle.gitversioner.IntegrationTest;
import pt.com.hugodias.gradle.gitversioner.core.version.Version;
import pt.com.hugodias.gradle.gitversioner.core.version.Versioner;
import pt.com.hugodias.gradle.gitversioner.core.version.VersionerConfig;

@IntegrationTest
public class VersionerTest {
  private File projectDir = new File("build/tmp/integrationTest/local");
  private Git git;

  @BeforeEach
  public void setUp() throws GitAPIException, IOException {
    projectDir.mkdirs();
    Git.init().setDirectory(projectDir).call();
    git = Git.open(new File(projectDir.getAbsolutePath() + "/.git"));
  }

  @AfterEach
  public void tearDown() throws IOException {
    git.close();
    FileUtils.deleteDirectory(projectDir);
  }

  @Test
  @DisplayName("Increments major version for commit messages matching default major regex")
  public void testIncrementsMajorVersionForCommitMatchingMajorRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("major", 3);
    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMajor()).isEqualTo(3);
  }

  @Test
  @DisplayName("Increments minor version for commit messages matching default minor regex")
  public void testIncrementsMinorVersionForCommitMatchingMinorRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("minor", 2);
    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMinor()).isEqualTo(2);
  }

  @Test
  @DisplayName("Increments patch version for commit messages matching default patch regex")
  public void testIncrementsPatchVersionForCommitMatchingPatchRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("patch", 1);
    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getPatch()).isEqualTo(1);
  }

  @Test
  @DisplayName("Increments commit version for commit messages matching not matching any regex")
  public void testIncrementsCommitVersionForCommitNotMatchingAnyRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("hello", 4);
    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getCommit()).isEqualTo(4);
  }

  @Test
  @DisplayName("Major version increment resets minor, patch, and commit versions")
  public void testMajorVersionIncrementResetsMinorPatchCommit() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("hello", 1);
    givenRepositoryHasTypeCommitsNumbering("patch", 1);
    givenRepositoryHasTypeCommitsNumbering("minor", 1);
    givenRepositoryHasTypeCommitsNumbering("major", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(0);
    assertThat(version.getPatch()).isEqualTo(0);
    assertThat(version.getCommit()).isEqualTo(0);
  }

  @Test
  @DisplayName("Minor version increment resets patch, and commit versions")
  public void testMinorVersionIncrementResetsPatchCommit() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("hello", 1);
    givenRepositoryHasTypeCommitsNumbering("patch", 1);
    givenRepositoryHasTypeCommitsNumbering("minor", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMinor()).isEqualTo(1);
    assertThat(version.getPatch()).isEqualTo(0);
    assertThat(version.getCommit()).isEqualTo(0);
  }

  @Test
  @DisplayName("Patch version increment resets commit versions")
  public void testPatchVersionIncrementResetsCommit() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("hello", 1);
    givenRepositoryHasTypeCommitsNumbering("patch", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getPatch()).isEqualTo(1);
    assertThat(version.getCommit()).isEqualTo(0);
  }

  @Test
  @DisplayName("Commit version increment resets nothing")
  public void testCommitVersionResetsNothing() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("major", 1);
    givenRepositoryHasTypeCommitsNumbering("minor", 1);
    givenRepositoryHasTypeCommitsNumbering("patch", 1);
    givenRepositoryHasTypeCommitsNumbering("hello", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(1);
    assertThat(version.getPatch()).isEqualTo(1);
    assertThat(version.getCommit()).isEqualTo(1);
  }

  @Test
  @DisplayName("Works even when there's loads of commits")
  public void testorksithLotsOfCommits() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("major", 100);
    givenRepositoryHasTypeCommitsNumbering("minor", 100);
    givenRepositoryHasTypeCommitsNumbering("patch", 100);
    givenRepositoryHasTypeCommitsNumbering("hello", 100);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMajor()).isEqualTo(100);
    assertThat(version.getMinor()).isEqualTo(100);
    assertThat(version.getPatch()).isEqualTo(100);
    assertThat(version.getCommit()).isEqualTo(100);
  }

  @Test
  @DisplayName("Increments major version from point specified in configuration")
  public void testIncrementsMajorFromPoint() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("major", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().startFromMajor(1).build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMajor()).isEqualTo(2);
  }

  @Test
  @DisplayName("Increments minor version from point specified in configuration")
  public void testIncrementsMinorFromPoint() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("minor", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().startFromMinor(2).build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMinor()).isEqualTo(3);
  }

  @Test
  @DisplayName("Increments patch version from point specified in configuration")
  public void testIncrementsPatchFromPoint() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("patch", 3);

    VersionerConfig versionerConfig = VersionerConfig.builder().startFromPatch(1).build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getPatch()).isEqualTo(4);
  }

  @Test
  @DisplayName("Increments major version based on major match regex specified in configuration")
  public void testIncrementsMajorBasedMajorRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("trex", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().matchMajor("trex").build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMajor()).isEqualTo(1);
  }

  @Test
  @DisplayName("Increments minor version based on minor match regex specified in configuration")
  public void testIncrementsMinorBasedMinorRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("stego", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().matchMinor("stego").build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getMinor()).isEqualTo(1);
  }

  @Test
  @DisplayName("Increments patch version based on patch match regex specified in configuration")
  public void testIncrementsPatchBasedPatchRegex() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("compy", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().matchPatch("compy").build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getPatch()).isEqualTo(1);
  }

  @Test
  @DisplayName("Version includes current branch")
  public void testVersionIncludesCurrentBranch() throws GitAPIException {
    givenRepositoryHasTypeCommitsNumbering("hello", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getBranch()).isEqualTo("master");
  }

  @Test
  @DisplayName("Version includes commit hash from HEAD")
  public void testVersionIncludesCommitHashFromHead() throws GitAPIException, IOException {
    givenRepositoryHasTypeCommitsNumbering("hello", 1);

    VersionerConfig versionerConfig = VersionerConfig.builder().build();
    Versioner versioner = Versioner.builder().gitFolder(projectDir).build();
    Version version = versioner.version(versionerConfig);
    assertThat(version.getHash())
        .isEqualTo(git.getRepository().findRef("HEAD").getObjectId().getName());
  }

  private void givenRepositoryHasTypeCommitsNumbering(String message, int number)
      throws GitAPIException {
    createCommits(String.format("[%s]", message), number);
  }

  private void createCommits(String message, int number) throws GitAPIException {
    for (int i = 0; i < number; i++) {
      git.commit().setSign(false).setAllowEmpty(true).setMessage(message).call();
    }
  }
}
