/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.util;

import java.io.File;
import lombok.Builder;
import lombok.Data;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

@Data
@Builder
public class Gradle {
  private final File directory;

  public BuildResult runTask(String name) {
    return GradleRunner.create()
        .withProjectDir(directory)
        .withArguments(name, "-q")
        .withPluginClasspath()
        .forwardOutput()
        .build();
  }
}
