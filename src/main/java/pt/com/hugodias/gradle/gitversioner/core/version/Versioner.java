/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.core.version;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

@Data
@Builder
public class Versioner {
  private File gitFolder;

  public Version version(VersionerConfig config) {
    try {
      var major = config.getStartFromMajor();
      var minor = config.getStartFromMinor();
      var patch = config.getStartFromPatch();
      var commit = 0;

      val git = Git.open(gitFolder);
      val branch = git.getRepository().getBranch();
      val hash = git.getRepository().findRef("HEAD").getObjectId().getName();

      val all = reverse(git.log().call());

      for (val it : all) {
        if (it.getFullMessage().contains(config.getMatchMajor())) {
          major++;
          minor = 0;
          patch = 0;
          commit = 0;
        } else if (it.getFullMessage().contains(config.getMatchMinor())) {
          minor++;
          patch = 0;
          commit = 0;
        } else if (it.getFullMessage().contains(config.getMatchPatch())) {
          patch++;
          commit = 0;
        } else {
          commit++;
        }
      }

      return Version.builder()
          .major(major)
          .minor(minor)
          .patch(patch)
          .commit(commit)
          .branch(branch)
          .hash(hash)
          .build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (NoHeadException e) {
      throw new RuntimeException(e);
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> List<T> reverse(Iterable<T> iterator) {
    var list = new LinkedList<T>();
    for (T value : iterator) {
      list.addFirst(value);
    }
    return list;
  }
}
