package run.scatter.botjde.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.scatter.botjde.events.director.service.PocketCommandHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PokeCommandTest {

  private PokeCommand pokeCommand;

  @BeforeEach
  void setUp() {
    pokeCommand = new PokeCommand(mock(PocketCommandHandler.class));
  }

  @Test
  void getName_returnsPoke() {
    assertThat(pokeCommand.getName()).isEqualTo("poke");
  }

  @Test
  void getCommandDefinition_hasCorrectName() {
    assertThat(pokeCommand.getCommandDefinition().name()).isEqualTo("poke");
  }

  @Test
  void getCommandDefinition_hasDescription() {
    assertThat(pokeCommand.getCommandDefinition().description().get()).isNotBlank();
  }
}
