/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public interface Tag {
  Property<String> getPrefix();

  Property<Boolean> getUseCommitMessage();
}
