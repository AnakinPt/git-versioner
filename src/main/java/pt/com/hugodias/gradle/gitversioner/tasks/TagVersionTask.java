/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.tasks;

import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import pt.com.hugodias.gradle.gitversioner.core.tag.GitTagger;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public abstract class TagVersionTask extends DefaultTask {
  @Input
  public abstract Property<String> getVersion();

  @Setter private GitTagger gitTagger;

  @TaskAction
  public void tagVersion() {
    gitTagger.tag(getVersion().get());
  }
}
