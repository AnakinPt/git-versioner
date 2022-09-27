/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.configuration;

import javax.inject.Inject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.Nested;
import pt.com.hugodias.gradle.gitversioner.configuration.git.Git;
import pt.com.hugodias.gradle.gitversioner.core.version.Version;
import pt.com.hugodias.gradle.gitversioner.core.version.Versioner;
import pt.com.hugodias.gradle.gitversioner.core.version.VersionerConfig;

@Data
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public abstract class VersionerExtension {
  protected final Project project;
  protected final Versioner versioner;
  protected String calculatedVersion;

  @Nested
  public abstract StartFrom getStartFrom();

  @Nested
  public abstract Match getMatch();

  @Nested
  public abstract Tag getTag();

  @Nested
  public abstract Git getGit();

  @Nested
  public abstract Pattern getPattern();

  public void startFrom(Action<? super StartFrom> action) {
    action.execute(getStartFrom());
  }

  public void match(Action<? super Match> action) {
    action.execute(getMatch());
  }

  public void tag(Action<? super Tag> action) {
    action.execute(getTag());
  }

  public void git(Action<? super Git> action) {
    action.execute(getGit());
  }

  public void pattern(Action<? super Pattern> action) {
    action.execute(getPattern());
  }

  public void apply() {
    VersionerConfig config = VersionerConfig.fromExtension(this);
    Version version = versioner.version(config);
    String stringVersion = version.print(config.getPattern());
    project.setVersion(stringVersion);
    setCalculatedVersion(stringVersion);
  }
}
