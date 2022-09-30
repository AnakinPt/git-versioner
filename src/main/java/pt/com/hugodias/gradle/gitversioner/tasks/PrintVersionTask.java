/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public abstract class PrintVersionTask extends DefaultTask {
  @Input
  public abstract Property<String> getVersion();

  @TaskAction
  @SuppressWarnings("java:S106")
  public void printVersion() {
    System.out.println(getVersion().get());
  }
}
