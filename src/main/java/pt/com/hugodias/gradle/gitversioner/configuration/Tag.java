/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public abstract class Tag {
  public abstract Property<String> getPrefix();

  public abstract Property<Boolean> getUseCommitMessage();
}
