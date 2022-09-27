/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration.git;

import org.gradle.api.provider.Property;

public abstract class Https {
  public abstract Property<String> getUsername();

  public abstract Property<String> getPassword();

  public abstract Property<String> getToken();
}
