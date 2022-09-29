/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration.git;

import org.gradle.api.provider.Property;

public interface Https {
  Property<String> getUsername();

  Property<String> getPassword();

  Property<String> getToken();
}
