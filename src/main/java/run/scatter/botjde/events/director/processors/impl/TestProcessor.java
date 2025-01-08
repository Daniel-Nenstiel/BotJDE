package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.entity.Anniversary;
import run.scatter.botjde.entity.Birthday;
import run.scatter.botjde.events.director.processors.CommandProcessor;
import run.scatter.botjde.scheduled.anniversary.dao.AnniversaryDao;
import run.scatter.botjde.scheduled.birthday.dao.BirthdayDao;

import java.time.LocalDate;
import java.util.List;

@Component
public class TestProcessor implements CommandProcessor {

  @Autowired
  AnniversaryDao anniversaryDao;

  @Autowired
  BirthdayDao birthdayDao;

  @Override
  public boolean supports(String command) {
    return "!test".equalsIgnoreCase(command) || "!wip".equalsIgnoreCase(command);
  }

  @Override
  public Mono<Void> process(String content, String author, Mono<MessageChannel> channelMono) {
    if (content.contains("!test")) {
      return pingResponse(channelMono);
    } else {
      return wipResponse(content, author, channelMono);
    }
  }

  Mono<Void> pingResponse(Mono<MessageChannel> channelMono) {
    return channelMono
      .flatMap(channel -> channel.createMessage("ping"))
      .then();
  }

  Mono<Void> wipResponse(String content, String author, Mono<MessageChannel> channelMono) {
    List<Anniversary> anniversaries = anniversaryDao.getTodaysAnniversaries();
    List<Birthday> birthday = birthdayDao.getBirthdays(LocalDate.of(1991, 7, 23));

    return channelMono
      .flatMap(channel -> channel.createMessage(
        String.format(
          "Happy Birthday to %s (<@%s>)",
            birthday.get(0).getUser().getName(),
            birthday.get(0).getUser().getDiscordId().asString()
          )
      )).then();
  }
}
