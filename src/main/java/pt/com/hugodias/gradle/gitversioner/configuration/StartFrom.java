/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public abstract class StartFrom {
  public abstract Property<Integer> getMajor();

  public abstract Property<Integer> getMinor();

  public abstract Property<Integer> getPatch();
}
