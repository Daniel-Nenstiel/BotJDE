package run.scatter.botjde.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.scheduled.puzzle.PuzzleMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PuzzleCommandTest {

  private PuzzleCommand puzzleCommand;

  @BeforeEach
  void setUp() {
    puzzleCommand = new PuzzleCommand(mock(PuzzleMessage.class));
  }

  @Test
  void getName_returnsPuzzle() {
    assertThat(puzzleCommand.getName()).isEqualTo("puzzle");
  }

  @Test
  void getCommandDefinition_hasCorrectName() {
    assertThat(puzzleCommand.getCommandDefinition().name()).isEqualTo("puzzle");
  }

  @Test
  void getCommandDefinition_hasDescription() {
    assertThat(puzzleCommand.getCommandDefinition().description().get()).isNotBlank();
  }
}
