/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import org.gradle.api.provider.Property;

public interface Match {
  Property<String> getMajor();

  Property<String> getMinor();

  Property<String> getPatch();
}
