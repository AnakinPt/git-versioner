/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public abstract class Match {
  public abstract Property<String> getMajor();

  public abstract Property<String> getMinor();

  public abstract Property<String> getPatch();
}
