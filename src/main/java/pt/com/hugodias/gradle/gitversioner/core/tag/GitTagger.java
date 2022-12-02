/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core.tag;

import com.jcraft.jsch.JSch;
import java.io.File;
import java.io.IOException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import pt.com.hugodias.gradle.gitversioner.core.exception.TaggingException;

@Data
@Builder
@Slf4j
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
        log.debug("Using last commit message");
        tagCommand.setMessage(getLastCommitMessage(git));
      }
      tagCommand.call();
      log.debug("Pushing the new tag with name " + prefixedVersion);
      log.debug("Credential Provider " + credentialsProvider.toString());
      git.push().add(prefixedVersion).setCredentialsProvider(credentialsProvider).call();
    } catch (IOException | GitAPIException e) {
      throw new TaggingException(e);
    }
  }

  private void configureHostChecking(TaggerConfig config) {
    JSch.setConfig("StrictHostKeyChecking", config.isStrictHostChecking() ? "yes" : "no");
    log.debug("Adding StrictHostKeyChecking with " + config.isStrictHostChecking());
  }

  private CredentialsProvider createCredentialsProvider(TaggerConfig config) {
    if (config.getUsername() != null) {
      log.debug("Creating a Credential Provider with username and password");
      log.trace("Username: " + config.getUsername());
      return new UsernamePasswordCredentialsProvider(config.getUsername(), config.getPassword());
    }
    if (config.getToken() != null) {
      log.debug("Creating a Credential Provider with token");
      return new UsernamePasswordCredentialsProvider(config.getToken(), "");
    }
    log.debug("Creating the default Credential Provider");
    return CredentialsProvider.getDefault();
  }

  private String getLastCommitMessage(Git git) throws GitAPIException {
    return git.log().setMaxCount(1).call().iterator().next().getFullMessage();
  }
}
