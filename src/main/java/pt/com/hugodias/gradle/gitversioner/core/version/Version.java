/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core.version;

import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@Builder
public class Version {
  private int major;
  private int minor;
  private int patch;
  private int commit;
  private String branch;
  private String hash;

  public String print(String pattern) {
    val filledVersion =
        pattern
            .replace("%M", String.valueOf(major))
            .replace("%m", String.valueOf(minor))
            .replace("%p", String.valueOf(patch))
            .replace("%c", String.valueOf(commit))
            .replace("%b", removeSlashes(branch))
            .replace("%H", hash)
            .replace("%h", hash.isEmpty() ? "" : hash.substring(0, 7));
    return commit != 0 ? removeParentheses(filledVersion) : removeCommitConditionals(filledVersion);
  }

  private String removeCommitConditionals(String version) {
    return version.replaceAll("\\(.*\\)", "");
  }

  private String removeParentheses(String version) {
    return version.replace("(", "").replace(")", "");
  }

  private String removeSlashes(String branch) {
    return branch.replaceAll("\\/", "_");
  }
}
