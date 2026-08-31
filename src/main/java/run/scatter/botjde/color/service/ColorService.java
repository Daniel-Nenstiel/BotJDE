package run.scatter.botjde.color.service;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.RoleCreateSpec;
import discord4j.rest.util.PermissionSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.scatter.botjde.color.model.DiscordColorPalette;
import run.scatter.botjde.color.model.DiscordColorPalette.ResolvedColor;
import run.scatter.botjde.color.model.DiscordColorPalette.Swatch;

import java.util.List;
import java.util.Optional;

/**
 * Service that manages Discord member color roles.
 */
@Slf4j
@Service
public class ColorService {

  public static final String COLOR_ROLE_PREFIX = "color-";

  public List<Swatch> getPalette() {
    return DiscordColorPalette.SWATCHES;
  }

  /**
   * Assigns a custom color role to a guild member.
   *
   * @param member the Discord member
   * @param colorInput color name or hex code
   * @return a Mono emitting the applied ResolvedColor
   */
  public Mono<ResolvedColor> setColor(Member member, String colorInput) {
    final Optional<ResolvedColor> resolvedOpt = DiscordColorPalette.resolve(colorInput);
    if (resolvedOpt.isEmpty()) {
      return Mono.error(new IllegalArgumentException(
          "Invalid color: `" + colorInput + "`. Please provide a valid preset name (e.g. `teal`, `red`) or 6-character hex code (e.g. `#FF5733`)."
      ));
    }

    final ResolvedColor resolvedColor = resolvedOpt.get();
    return applyColor(member, resolvedColor);
  }

  /**
   * Assigns a randomly generated vibrant color to a guild member.
   *
   * @param member the Discord member
   * @return a Mono emitting the applied ResolvedColor
   */
  public Mono<ResolvedColor> setRandomColor(Member member) {
    final ResolvedColor randomColor = DiscordColorPalette.random();
    return applyColor(member, randomColor);
  }

  /**
   * Removes all color roles from a guild member.
   *
   * @param member the Discord member
   * @return a Mono completing when all color roles are removed
   */
  public Mono<Void> removeColor(Member member) {
    return member.getRoles()
        .filter(this::isColorRole)
        .flatMap(role -> {
          log.info("Removing color role '{}' from user '{}'", role.getName(), member.getUsername());
          return member.removeRole(role.getId());
        })
        .then();
  }

  private Mono<ResolvedColor> applyColor(Member member, ResolvedColor resolvedColor) {
    final String targetRoleName = COLOR_ROLE_PREFIX + resolvedColor.hex().toUpperCase();

    return removeColor(member)
        .then(member.getGuild())
        .flatMap(guild -> findOrCreateRole(guild, targetRoleName, resolvedColor))
        .flatMap(role -> {
          log.info("Assigning color role '{}' to user '{}'", role.getName(), member.getUsername());
          return member.addRole(role.getId()).thenReturn(resolvedColor);
        });
  }

  private Mono<Role> findOrCreateRole(Guild guild, String roleName, ResolvedColor resolvedColor) {
    return guild.getRoles()
        .filter(r -> r.getName() != null && r.getName().equalsIgnoreCase(roleName))
        .next()
        .switchIfEmpty(
            Mono.defer(() -> guild.createRole(RoleCreateSpec.builder()
                .name(roleName)
                .color(resolvedColor.color())
                .permissions(PermissionSet.none())
                .hoist(false)
                .mentionable(false)
                .build()))
        );
  }

  private boolean isColorRole(Role role) {
    return role.getName() != null && role.getName().toLowerCase().startsWith(COLOR_ROLE_PREFIX);
  }
}
