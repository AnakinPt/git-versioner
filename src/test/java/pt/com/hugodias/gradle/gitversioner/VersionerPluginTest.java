/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

public class VersionerPluginTest {

  @Test
  public void greetingTest() {
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("pt.com.hugo-dias.git-versioner");

    assertTrue(project.getPluginManager().hasPlugin("pt.com.hugo-dias.git-versioner"));

    assertNotNull(project.getTasks().getByName("printVersion"));
    assertNotNull(project.getTasks().getByName("tagVersion"));
  }
}
