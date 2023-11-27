/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner;

import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import pt.com.hugodias.gradle.gitversioner.configuration.VersionerExtension;
import pt.com.hugodias.gradle.gitversioner.core.tag.GitTagger;
import pt.com.hugodias.gradle.gitversioner.core.tag.TaggerConfig;
import pt.com.hugodias.gradle.gitversioner.core.version.Versioner;
import pt.com.hugodias.gradle.gitversioner.core.version.VersionerConfig;
import pt.com.hugodias.gradle.gitversioner.tasks.PrintVersionTask;
import pt.com.hugodias.gradle.gitversioner.tasks.TagVersionTask;

public class VersionerPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    File gitFolder = findGitFolder(project.getRootDir());
    Versioner versioner = Versioner.builder().gitFolder(gitFolder).build();
    VersionerExtension extension =
        project.getExtensions().create("versioner", VersionerExtension.class, project, versioner);
    TaskProvider<PrintVersionTask> printVersionTask =
        project.getTasks().register("printVersion", PrintVersionTask.class);
    TaskProvider<TagVersionTask> tagVersionTask =
        project
            .getTasks()
            .register(
                "tagVersion",
                TagVersionTask.class,
                task -> {
                  GitTagger tagger =
                      GitTagger.builder()
                          .gitFolder(gitFolder)
                          .config(TaggerConfig.fromExtension(extension))
                          .build();
                  task.setGitTagger(tagger);
                });

    project.afterEvaluate(
        innerProject -> {
          if (extension.getCalculatedVersion() == null) {
            VersionerConfig config = VersionerConfig.fromExtension(extension);
            String version = versioner.version(config).print(config.getPattern());
            innerProject.setVersion(version);
          }

          printVersionTask.get().getVersion().set(innerProject.getVersion().toString());
          tagVersionTask.get().getVersion().set(innerProject.getVersion().toString());
        });
  }

  File findGitFolder(File folder) {
      if(folder == null) {
          throw new IllegalStateException("No .git directory found in path tree");
      }
      File gitFolder = new File(folder, ".git");
      return gitFolder.exists() ? gitFolder : findGitFolder(folder.getParentFile());
  }
}
