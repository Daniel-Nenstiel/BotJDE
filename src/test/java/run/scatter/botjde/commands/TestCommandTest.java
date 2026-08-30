package run.scatter.botjde.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestCommandTest {

  private TestCommand testCommand;

  @BeforeEach
  void setUp() {
    testCommand = new TestCommand();
  }

  @Test
  void getName_returnsTest() {
    assertThat(testCommand.getName()).isEqualTo("test");
  }

  @Test
  void getCommandDefinition_hasCorrectName() {
    assertThat(testCommand.getCommandDefinition().name()).isEqualTo("test");
  }

  @Test
  void getCommandDefinition_hasDescription() {
    assertThat(testCommand.getCommandDefinition().description().get()).isNotBlank();
  }
}
