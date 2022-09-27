/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration.git;

import org.gradle.api.tasks.Nested;

public abstract class Git {
  @Nested
  public abstract Authentication getAuthentication();
}
