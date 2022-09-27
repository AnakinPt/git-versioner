/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner;

import java.io.File;
import lombok.val;
import org.gradle.api.Action;
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
    File gitFolder = new File(project.getRootDir() + "/.git");
    Versioner versioner = Versioner.builder().gitFolder(gitFolder).build();
    VersionerExtension extension =
        project.getExtensions().create("versioner", VersionerExtension.class, project, versioner);
    TaskProvider<PrintVersionTask> printVersionTask =
        project.getTasks().register("printVersion", PrintVersionTask.class);
    GitTagger tagger =
        GitTagger.builder()
            .gitFolder(gitFolder)
            .config(TaggerConfig.fromExtension(extension))
            .build();
    TaskProvider<TagVersionTask> tagVersionTask =
        project.getTasks().register("tagVersion", TagVersionTask.class, tagger);

    project.afterEvaluate(
        new Action<Project>() {
          @Override
          public void execute(Project project) {
            if (extension.getCalculatedVersion() == null) {
              val config = VersionerConfig.fromExtension(extension);
              val version = versioner.version(config).print(config.getPattern());
              project.setVersion(version);
            }

            printVersionTask.get().getVersion().set(project.getVersion().toString());
            tagVersionTask.get().getVersion().set(project.getVersion().toString());
          }
        });
  }
}
