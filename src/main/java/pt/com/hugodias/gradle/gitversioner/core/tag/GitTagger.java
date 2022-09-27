/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core.tag;

import com.jcraft.jsch.JSch;
import java.io.File;
import java.io.IOException;
import lombok.Builder;
import lombok.Data;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@Data
@Builder
public class GitTagger {
  private File gitFolder;
  private TaggerConfig config;

  public void tag(String version) {
    try (Git git = Git.open(gitFolder)) {
      configureHostChecking(config);
      CredentialsProvider credentialsProvider = createCredentialsProvider(config);
      String prefixedVersion = config.getPrefix() + version;
      TagCommand tagCommand = git.tag().setName(prefixedVersion);
      if (config.isUseCommitMessage()) {
        tagCommand.setMessage(getLastCommitMessage(git));
      }
      tagCommand.call();
      git.push().add(prefixedVersion).setCredentialsProvider(credentialsProvider).call();
    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void configureHostChecking(TaggerConfig config) {
    JSch.setConfig("StrictHostKeyChecking", config.isStrictHostChecking() ? "yes" : "no");
  }

  private CredentialsProvider createCredentialsProvider(TaggerConfig config) {
    if (config.getUsername() != null) {
      return new UsernamePasswordCredentialsProvider(config.getUsername(), config.getPassword());
    }
    if (config.getToken() != null) {
      return new UsernamePasswordCredentialsProvider(config.getToken(), "");
    }
    return CredentialsProvider.getDefault();
  }

  private String getLastCommitMessage(Git git) throws GitAPIException {
    return git.log().setMaxCount(1).call().iterator().next().getFullMessage();
  }
}
