/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration.git;

import org.gradle.api.tasks.Nested;

public interface Authentication {
  @Nested
  Ssh getSsh();

  @Nested
  Https getHttps();
}
