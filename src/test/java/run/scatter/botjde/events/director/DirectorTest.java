package run.scatter.botjde.events.director;

import discord4j.core.object.entity.channel.MessageChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class DirectorTest {

  @Mock
  private CommandProcessor processor1;

  @Mock
  private CommandProcessor processor2;

  @Mock
  private MessageChannel messageChannel;

  private Director director;

  @BeforeEach
  void setUp() {
    try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
      director = new Director(List.of(processor1, processor2));
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize mocks", e);
    }
  }

  @Test
  void testDirectCall_withValidCommand_callsCorrectProcessor() {
    // Arrange
    String content = "!testCommand arg1";
    String author = "user";
    Mono<MessageChannel> channelMono = Mono.just(messageChannel);

    // Mock the behavior of the processors
    when(processor1.supports("!testCommand")).thenReturn(true);
    when(processor1.process(content, author, channelMono)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = director.directCall(content, author, channelMono);

    // Assert
    result.block(); // Blocking to wait for the result
    verify(processor1, times(1)).process(content, author, channelMono); // Ensure process is called on processor1
    verify(processor2, never()).process(any(), any(), any()); // Ensure processor2 is not called
  }

  @Test
  void testDirectCall_withInvalidCommand_returnsEmpty() {
    // Arrange
    String content = "!unknownCommand";
    String author = "user";
    Mono<MessageChannel> channelMono = Mono.just(messageChannel);

    // Act
    Mono<Void> result = director.directCall(content, author, channelMono);

    // Assert
    assertNull(result.block()); // Ensure it returns Mono.empty()
    verify(processor1, never()).process(any(), any(), any()); // Ensure no processors were invoked
    verify(processor2, never()).process(any(), any(), any()); // Ensure no processors were invoked
  }

  @Test
  void testDirectCall_withCommandNotStartingWithExclamation_returnsEmpty() {
    // Arrange
    String content = "testCommand arg1";
    String author = "user";
    Mono<MessageChannel> channelMono = Mono.just(messageChannel);

    // Act
    Mono<Void> result = director.directCall(content, author, channelMono);

    // Assert
    assertNull(result.block()); // Ensure it returns Mono.empty()
    verify(processor1, never()).process(any(), any(), any()); // Ensure no processors were invoked
    verify(processor2, never()).process(any(), any(), any()); // Ensure no processors were invoked
  }

  @Test
  void testDirectCall_withNoProcessorFound_returnsEmpty() {
    // Arrange
    String content = "!testCommand arg1";
    String author = "user";
    Mono<MessageChannel> channelMono = Mono.just(messageChannel);

    // Mock the behavior of the processors (none support the command)
    when(processor1.supports("!testCommand")).thenReturn(false);
    when(processor2.supports("!testCommand")).thenReturn(false);

    // Act
    Mono<Void> result = director.directCall(content, author, channelMono);

    // Assert
    assertNull(result.block()); // Ensure it returns Mono.empty()
    verify(processor1, never()).process(any(), any(), any()); // Ensure no processors were invoked
    verify(processor2, never()).process(any(), any(), any()); // Ensure no processors were invoked
  }
}
