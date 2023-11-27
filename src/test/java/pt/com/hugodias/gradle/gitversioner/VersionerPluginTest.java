/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class VersionerPluginTest {

  @Test
  void greetingTest() {
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("pt.com.hugo-dias.git-versioner");

    assertTrue(project.getPluginManager().hasPlugin("pt.com.hugo-dias.git-versioner"));

    assertNotNull(project.getTasks().getByName("printVersion"));
    assertNotNull(project.getTasks().getByName("tagVersion"));
  }

  @Test
  void testFindGitFolderInCurrentDir() {
    Project project = ProjectBuilder.builder().build();
    File gitDir = new File(project.getRootDir() + "/.git");
    assertTrue(gitDir.mkdirs());

    assertEquals(gitDir, new VersionerPlugin().findGitFolder(project.getRootDir()));
  }

  @Test
  void testFindGitFolderInParentDir() {
    Project project = ProjectBuilder.builder().build();
    File gitDir = new File(project.getRootDir() + "/.git");
    File testRootDir = new File(project.getRootDir() + "/sub1/sub2");
    assertTrue(gitDir.mkdirs());
    assertTrue(testRootDir.mkdirs());

    assertEquals(gitDir, new VersionerPlugin().findGitFolder(testRootDir));
  }
}
