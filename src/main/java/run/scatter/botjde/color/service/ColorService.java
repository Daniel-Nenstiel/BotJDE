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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

  /**
   * Cleans up orphan and duplicate color roles.
   * Consolidates duplicate roles of the same color to a single canonical role and deletes unused roles.
   *
   * @param guild the Discord guild
   * @return a Mono emitting the total number of deleted orphan and duplicate roles
   */
  public Mono<Long> cleanupOrphanColorRoles(Guild guild) {
    return Mono.zip(guild.getMembers().collectList(), guild.getRoles().filter(this::isColorRole).collectList())
        .flatMap(tuple -> {
          final List<Member> members = tuple.getT1();
          final List<Role> colorRoles = tuple.getT2();

          if (colorRoles.isEmpty()) {
            return Mono.just(0L);
          }

          final Map<Integer, List<Role>> rolesByColor = colorRoles.stream()
              .collect(Collectors.groupingBy(r -> r.getColor() != null ? r.getColor().getRGB() : 0));

          return Flux.fromIterable(rolesByColor.values())
              .flatMap(group -> processColorRoleGroup(members, group))
              .reduce(0L, Long::sum);
        });
  }

  private Mono<Long> processColorRoleGroup(List<Member> members, List<Role> group) {
    if (group.isEmpty()) {
      return Mono.just(0L);
    }

    // Prefer a role with canonical name format "color-#RRGGBB"
    final Role primaryRole = group.stream()
        .filter(r -> r.getName() != null && r.getName().matches("^color-#[0-9a-fA-F]{6}$"))
        .findFirst()
        .orElse(group.get(0));

    final List<Role> duplicateRoles = group.stream()
        .filter(r -> !r.getId().equals(primaryRole.getId()))
        .toList();

    // 1. Migrate any members from duplicate roles to the primary role, then delete duplicate roles
    final Flux<Long> cleanDuplicates = Flux.fromIterable(duplicateRoles)
        .flatMap(dupRole -> {
          final List<Member> membersWithDup = members.stream()
              .filter(m -> m.getRoleIds().contains(dupRole.getId()))
              .toList();

          return Flux.fromIterable(membersWithDup)
              .flatMap(m -> m.addRole(primaryRole.getId())
                  .then(m.removeRole(dupRole.getId())))
              .then(deleteRoleSafe(dupRole, "Consolidating duplicate color role into " + primaryRole.getName()));
        });

    return cleanDuplicates.reduce(0L, Long::sum)
        .flatMap(cleanedDupesCount -> {
          // 2. Check if primaryRole has any members assigned (including originally or newly migrated)
          final boolean hasMembers = members.stream()
              .anyMatch(m -> m.getRoleIds().contains(primaryRole.getId())
                  || duplicateRoles.stream().anyMatch(dup -> m.getRoleIds().contains(dup.getId())));

          if (!hasMembers) {
            return deleteRoleSafe(primaryRole, "Color cleanup: no members assigned to this color role")
                .map(deleted -> cleanedDupesCount + deleted);
          }

          return Mono.just(cleanedDupesCount);
        });
  }

  private Mono<Long> deleteRoleSafe(Role role, String reason) {
    log.info("Deleting unused/duplicate color role '{}' ({})", role.getName(), role.getId().asString());
    return role.delete(reason)
        .thenReturn(1L)
        .onErrorResume(e -> {
          log.warn("Failed to delete color role '{}': {}", role.getName(), e.getMessage());
          return Mono.just(0L);
        });
  }

  private Mono<ResolvedColor> applyColor(Member member, ResolvedColor resolvedColor) {
    final String targetRoleName = COLOR_ROLE_PREFIX + resolvedColor.hex().toUpperCase();

    return member.getGuild()
        .flatMap(guild -> findOrCreateRole(guild, targetRoleName, resolvedColor)
            .flatMap(role -> positionRoleBelowBot(guild, role)))
        .flatMap(role -> removeOtherColorRoles(member, role)
            .then(member.addRole(role.getId()))
            .thenReturn(resolvedColor));
  }

  private Mono<Void> removeOtherColorRoles(Member member, Role keepRole) {
    return member.getRoles()
        .filter(r -> isColorRole(r) && !r.getId().equals(keepRole.getId()))
        .flatMap(role -> {
          log.info("Removing old color role '{}' from user '{}'", role.getName(), member.getUsername());
          return member.removeRole(role.getId());
        })
        .then();
  }

  private Mono<Role> findOrCreateRole(Guild guild, String roleName, ResolvedColor resolvedColor) {
    return guild.getRoles()
        .filter(r -> matchesColorRole(r, roleName, resolvedColor))
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

  private boolean matchesColorRole(Role role, String roleName, ResolvedColor resolvedColor) {
    if (!isColorRole(role)) {
      return false;
    }
    if (role.getName().equalsIgnoreCase(roleName)) {
      return true;
    }
    final String cleanRoleName = role.getName().toLowerCase().replace(COLOR_ROLE_PREFIX, "").replace("#", "");
    final String cleanTargetHex = resolvedColor.hex().toLowerCase().replace("#", "");
    if (!cleanRoleName.isEmpty() && cleanRoleName.equalsIgnoreCase(cleanTargetHex)) {
      return true;
    }
    return role.getColor() != null && role.getColor().getRGB() == resolvedColor.color().getRGB();
  }

  private Mono<Role> positionRoleBelowBot(Guild guild, Role role) {
    return guild.getSelfMember()
        .flatMap(Member::getHighestRole)
        .flatMap(botRole -> {
          final int targetPosition = Math.max(1, botRole.getRawPosition() - 1);
          if (role.getRawPosition() < targetPosition) {
            log.info("Elevating color role '{}' from position {} to {}", role.getName(), role.getRawPosition(), targetPosition);
            return role.changePosition(targetPosition)
                .then(Mono.just(role))
                .onErrorResume(e -> {
                  log.warn("Unable to elevate role '{}' position: {}", role.getName(), e.getMessage());
                  return Mono.just(role);
                });
          }
          return Mono.just(role);
        })
        .defaultIfEmpty(role);
  }

  private boolean isColorRole(Role role) {
    return role != null && role.getName() != null && role.getName().toLowerCase().startsWith(COLOR_ROLE_PREFIX);
  }
}
