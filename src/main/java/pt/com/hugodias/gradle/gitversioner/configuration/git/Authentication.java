/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration.git;

import org.gradle.api.tasks.Nested;

public abstract class Authentication {
  @Nested
  public abstract Ssh getSsh();

  @Nested
  public abstract Https getHttps();
}
