/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public interface StartFrom {
  Property<Integer> getMajor();

  Property<Integer> getMinor();

  Property<Integer> getPatch();
}
