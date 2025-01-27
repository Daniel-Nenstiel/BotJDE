package run.scatter.botjde.events.director.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PocketCommandHandler {
  public String handleCommand(String content, String author) {
    return "pika";
  }
}
