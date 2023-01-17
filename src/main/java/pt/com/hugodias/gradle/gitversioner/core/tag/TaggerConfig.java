/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core.tag;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pt.com.hugodias.gradle.gitversioner.configuration.VersionerExtension;

@Data
@Builder
@Slf4j
public class TaggerConfig {
  private String username;
  private String password;
  private String token;
  private boolean strictHostChecking;
  @Builder.Default private String prefix = "v";
  private boolean useCommitMessage;
  @Builder.Default private String remote = "origin";

  public static TaggerConfig fromExtension(VersionerExtension extension) {
    return TaggerConfig.builder()
        .username(extension.getGit().getAuthentication().getHttps().getUsername().getOrNull())
        .password(extension.getGit().getAuthentication().getHttps().getPassword().getOrNull())
        .token(extension.getGit().getAuthentication().getHttps().getToken().getOrNull())
        .strictHostChecking(
            extension.getGit().getAuthentication().getSsh().getStrictSsl().getOrElse(false))
        .prefix(extension.getTag().getPrefix().getOrElse("v"))
        .useCommitMessage(extension.getTag().getUseCommitMessage().getOrElse(false))
        .remote(extension.getGit().getRemote().getOrElse("origin"))
        .build();
  }
}
