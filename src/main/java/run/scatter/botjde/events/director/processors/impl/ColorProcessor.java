package run.scatter.botjde.events.director.processors.impl;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.scatter.botjde.events.director.processors.CommandProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ColorProcessor implements CommandProcessor {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#?([A-Fa-f0-9]{6})$");

    @Override
    public boolean supports(String command) {
        return command.equalsIgnoreCase("!color");
    }

    @Override
    public Mono<Void> process(String content, String author, Message message) {
        String[] parts = content.split(" ");
        if (parts.length < 2) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Usage: !color #RRGGBB"))
                    .then();
        }
        String hex = parts[1].replace("#", "");
        Matcher matcher = HEX_PATTERN.matcher(hex);
        if (!matcher.matches()) {
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Invalid hex code. Use format #RRGGBB."))
                    .then();
        }
        int rgb = Integer.parseInt(matcher.group(1), 16);
        Color color = Color.of(rgb);
        return message.getAuthorAsMember()
                .flatMap(member -> member.getGuild()
                        .flatMap(guild -> setOrUpdateColorRole(guild, member, color, message)))
                .onErrorResume(e -> message.getChannel()
                        .flatMap(channel -> channel.createMessage("Failed to set color: " + e.getMessage()))
                        .then());
    }

    private Mono<Void> setOrUpdateColorRole(Guild guild, Member member, Color color, Message message) {
        String roleName = "usercolor-" + member.getId().asString();
        // Remove old color roles
        return member.getRoles()
                .filter(role -> role.getName().startsWith("usercolor-"))
                .flatMap(role -> member.removeRole(role.getId()))
                .thenMany(guild.getRoles())
                .filter(role -> role.getName().equals(roleName))
                .singleOrEmpty()
                .switchIfEmpty(guild.createRole(spec -> {
                    spec.setName(roleName);
                    spec.setColor(color);
                    spec.setMentionable(false);
                    spec.setHoist(false);
                }))
                .flatMap(role -> role.edit(spec -> spec.setColor(color)))
                .flatMap(role -> member.addRole(role.getId()))
                .then(message.getChannel()
                        .flatMap(channel -> channel.createMessage("Color updated!")))
                .then();
    }
}
