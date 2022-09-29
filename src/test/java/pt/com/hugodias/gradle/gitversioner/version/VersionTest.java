/* (C) 2022 Hugo Dias */
package pt.com.hugodias.gradle.gitversioner.version;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pt.com.hugodias.gradle.gitversioner.IntegrationTest;
import pt.com.hugodias.gradle.gitversioner.core.version.Version;

@IntegrationTest
class VersionTest {

  private Version versionWithCommit =
      Version.builder()
          .major(1)
          .minor(2)
          .patch(3)
          .commit(4)
          .branch("mybranch")
          .hash("myhash123")
          .build();

  private Version versionWithoutCommit =
      Version.builder()
          .major(1)
          .minor(2)
          .patch(3)
          .commit(0)
          .branch("mybranch")
          .hash("myhash123")
          .build();

  @ParameterizedTest(name = "{index} pattern {0} prints the version correctly")
  @CsvSource(
      value = {
        "%M.%m.%p.%c,1.2.3.4",
        "%M.%m.%p-%c,1.2.3-4",
        "%M.%m.%p-%H,1.2.3-myhash123",
        "%M.%m.%p-%h,1.2.3-myhash1",
        "%M.%m.%p-%b,1.2.3-mybranch",
        "%M.%m.%p(-%c),1.2.3-4",
        "%M.%m.%p(-SNAPSHOT),1.2.3-SNAPSHOT"
      })
  @DisplayName("prints version correctly according to pattern")
  public void testWithCommit(String pattern, String expected) {
    assertThat(versionWithCommit.print(pattern)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "{index} pattern {0} prints the version correctly")
  @CsvSource(value = {"%M.%m.%p(-%b),1.2.3", "%M.%m.%p(-SNAPSHOT),1.2.3"})
  public void testWithoutCommit(String pattern, String expected) {
    assertThat(versionWithoutCommit.print(pattern)).isEqualTo(expected);
  }
}
