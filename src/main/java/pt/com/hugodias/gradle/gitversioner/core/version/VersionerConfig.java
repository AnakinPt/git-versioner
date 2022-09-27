/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core.version;

import lombok.Builder;
import lombok.Data;
import pt.com.hugodias.gradle.gitversioner.configuration.VersionerExtension;

@Data
@Builder
public class VersionerConfig {
  @Builder.Default private int startFromMajor = 0;
  @Builder.Default private int startFromMinor = 0;
  @Builder.Default private int startFromPatch = 0;
  @Builder.Default private String matchMajor = "[major]";
  @Builder.Default private String matchMinor = "[minor]";
  @Builder.Default private String matchPatch = "[patch]";
  @Builder.Default private String pattern = "%M.%m.%p(.%c)";
  @Builder.Default private String tagPrefix = "v";
  @Builder.Default private boolean tagUseCommitMessage = false;

  public static VersionerConfig fromExtension(VersionerExtension extension) {
    return VersionerConfig.builder()
        .startFromMajor(extension.getStartFrom().getMajor().getOrElse(0))
        .startFromMinor(extension.getStartFrom().getMinor().getOrElse(0))
        .startFromPatch(extension.getStartFrom().getPatch().getOrElse(0))
        .matchMajor(extension.getMatch().getMajor().getOrElse("[major]"))
        .matchMinor(extension.getMatch().getMinor().getOrElse("[minor]"))
        .matchPatch(extension.getMatch().getPatch().getOrElse("[patch]"))
        .pattern(extension.getPattern().getPattern().getOrElse("%M.%m.%p(.%c)"))
        .tagPrefix(extension.getTag().getPrefix().getOrElse("v"))
        .tagUseCommitMessage(extension.getTag().getUseCommitMessage().getOrElse(false))
        .build();
  }
}
