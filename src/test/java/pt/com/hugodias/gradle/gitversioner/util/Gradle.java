/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.util;

import java.io.File;
import java.io.IOException;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

@Data
@Builder
public class Gradle {
  private final File directory;

  public BuildResult runTask(String name) throws IOException {
    FileUtils.copyFile(
        new File("build/testkit/test/testkit-gradle.properties"),
        new File(directory + "/gradle.properties"));
    return GradleRunner.create()
        .withProjectDir(directory)
        .withArguments(name, "--q")
        .withPluginClasspath()
        .forwardOutput()
        .build();
  }
}
