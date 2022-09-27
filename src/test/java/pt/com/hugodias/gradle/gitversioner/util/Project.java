/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.util;

import java.io.File;
import java.io.IOException;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

@Data
@Builder
public class Project {
  private final File directory;
  private Git git;

  public Project withGit() throws GitAPIException {
    git = Git.init().setDirectory(directory).call();
    return this;
  }

  public Project withCommit(String message) throws GitAPIException {
    git.commit().setSign(false).setAllowEmpty(true).setMessage(message).call();
    return this;
  }

  public Project withGroovyGradleFile(String name) throws IOException {
    return withGradleFile(name + "-build.gradle", "build.gradle");
  }

  public Project withKotlinGradleFile(String name) throws IOException {
    return withGradleFile(name + "-build.gradle.kts", "build.gradle.kts");
  }

  public Project withSettingsFile() throws IOException {
    return withGradleFile("settings.gradle", "settings.gradle");
  }

  private Project withGradleFile(String name, String destination) throws IOException {
    FileUtils.copyFile(
        new File("src/test/resources/functionalTest/" + name),
        new File(directory + "/" + destination));
    return this;
  }
}
