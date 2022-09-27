/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public abstract class Pattern {
  public abstract Property<String> getPattern();
}
