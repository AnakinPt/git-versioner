/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration.git;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

public interface Git {
  @Nested
  Authentication getAuthentication();

  default void authentication(Action<? super Authentication> action) {
    action.execute(getAuthentication());
  }
}
