package run.scatter.botjde.color.service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.RoleCreateSpec;
import discord4j.rest.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.scatter.botjde.color.model.DiscordColorPalette.ResolvedColor;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ColorServiceTest {

  private ColorService colorService;
  private Member mockMember;
  private Guild mockGuild;

  @BeforeEach
  void setUp() {
    colorService = new ColorService();
    mockMember = mock(Member.class);
    mockGuild = mock(Guild.class);

    when(mockMember.getGuild()).thenReturn(Mono.just(mockGuild));
    when(mockMember.getRoles()).thenReturn(Flux.empty());
    when(mockMember.getUsername()).thenReturn("TestUser");
    when(mockMember.addRole(any(Snowflake.class))).thenReturn(Mono.empty());
    when(mockMember.removeRole(any(Snowflake.class))).thenReturn(Mono.empty());
    when(mockGuild.getSelfMember()).thenReturn(Mono.empty());
  }

  @Test
  void getPalette_returns16Swatches() {
    assertThat(colorService.getPalette()).hasSize(16);
  }

  @Test
  void setColor_withInvalidColor_emitsError() {
    assertThatThrownBy(() -> colorService.setColor(mockMember, "invalid-color").block())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid color");
  }

  @Test
  void setColor_withExistingRole_assignsExistingRole() {
    Role existingRole = mock(Role.class);
    when(existingRole.getName()).thenReturn("color-#1ABC9C");
    when(existingRole.getId()).thenReturn(Snowflake.of(111L));
    when(existingRole.getColor()).thenReturn(Color.of(0x1A, 0xBC, 0x9C));
    when(mockGuild.getRoles()).thenReturn(Flux.just(existingRole));
    when(mockMember.addRole(Snowflake.of(111L))).thenReturn(Mono.empty());

    ResolvedColor result = colorService.setColor(mockMember, "teal").block();

    assertThat(result).isNotNull();
    assertThat(result.hex()).isEqualTo("#1ABC9C");
    verify(mockMember, times(1)).addRole(Snowflake.of(111L));
    verify(mockGuild, never()).createRole(any(RoleCreateSpec.class));
  }

  @Test
  void setColor_withExistingRoleByRgbMatch_assignsExistingRoleWithoutCreatingNew() {
    Role legacyRole = mock(Role.class);
    when(legacyRole.getName()).thenReturn("color-teal");
    when(legacyRole.getId()).thenReturn(Snowflake.of(111L));
    when(legacyRole.getColor()).thenReturn(Color.of(0x1A, 0xBC, 0x9C));
    when(mockGuild.getRoles()).thenReturn(Flux.just(legacyRole));
    when(mockMember.addRole(Snowflake.of(111L))).thenReturn(Mono.empty());

    ResolvedColor result = colorService.setColor(mockMember, "#1ABC9C").block();

    assertThat(result).isNotNull();
    assertThat(result.hex()).isEqualTo("#1ABC9C");
    verify(mockMember, times(1)).addRole(Snowflake.of(111L));
    verify(mockGuild, never()).createRole(any(RoleCreateSpec.class));
  }

  @Test
  void setColor_withNonExistingRole_createsAndAssignsRole() {
    when(mockGuild.getRoles()).thenReturn(Flux.empty());
    Role newRole = mock(Role.class);
    when(newRole.getName()).thenReturn("color-#FF5733");
    when(newRole.getId()).thenReturn(Snowflake.of(222L));
    when(newRole.getColor()).thenReturn(Color.of(0xFF, 0x57, 0x33));
    when(mockGuild.createRole(any(RoleCreateSpec.class))).thenReturn(Mono.just(newRole));
    when(mockMember.addRole(Snowflake.of(222L))).thenReturn(Mono.empty());

    ResolvedColor result = colorService.setColor(mockMember, "#FF5733").block();

    assertThat(result).isNotNull();
    assertThat(result.hex()).isEqualTo("#FF5733");
    verify(mockGuild, times(1)).createRole(any(RoleCreateSpec.class));
    verify(mockMember, times(1)).addRole(Snowflake.of(222L));
  }

  @Test
  void setColor_elevatesRolePositionBelowBotRole() {
    Role colorRole = mock(Role.class);
    when(colorRole.getName()).thenReturn("color-#1ABC9C");
    when(colorRole.getId()).thenReturn(Snowflake.of(111L));
    when(colorRole.getColor()).thenReturn(Color.of(0x1A, 0xBC, 0x9C));
    when(colorRole.getRawPosition()).thenReturn(2);
    when(colorRole.changePosition(9)).thenReturn(Flux.just(colorRole));

    Member botMember = mock(Member.class);
    Role botRole = mock(Role.class);
    when(botRole.getRawPosition()).thenReturn(10);
    when(botMember.getHighestRole()).thenReturn(Mono.just(botRole));

    when(mockGuild.getSelfMember()).thenReturn(Mono.just(botMember));
    when(mockGuild.getRoles()).thenReturn(Flux.just(colorRole));
    when(mockMember.addRole(Snowflake.of(111L))).thenReturn(Mono.empty());

    ResolvedColor result = colorService.setColor(mockMember, "teal").block();

    assertThat(result).isNotNull();
    verify(colorRole, times(1)).changePosition(9);
  }

  @Test
  void removeColor_removesAllColorRoles() {
    Role colorRole1 = mock(Role.class);
    when(colorRole1.getName()).thenReturn("color-#1ABC9C");
    when(colorRole1.getId()).thenReturn(Snowflake.of(111L));

    Role normalRole = mock(Role.class);
    when(normalRole.getName()).thenReturn("Member");
    when(normalRole.getId()).thenReturn(Snowflake.of(999L));

    when(mockMember.getRoleIds()).thenReturn(Set.of(Snowflake.of(111L), Snowflake.of(999L)));

    colorService.removeColor(mockMember).block();

    verify(mockMember, times(1)).removeRole(Snowflake.of(111L));
    verify(mockMember, never()).removeRole(Snowflake.of(999L));
  }

  @Test
  void cleanupOrphanColorRoles_deletesOnlyUnusedColorRoles() {
    Role usedColorRole = mock(Role.class);
    when(usedColorRole.getName()).thenReturn("color-#1ABC9C");
    when(usedColorRole.getId()).thenReturn(Snowflake.of(111L));
    when(usedColorRole.getColor()).thenReturn(Color.of(0x1A, 0xBC, 0x9C));

    Role unusedColorRole = mock(Role.class);
    when(unusedColorRole.getName()).thenReturn("color-#E91E63");
    when(unusedColorRole.getId()).thenReturn(Snowflake.of(222L));
    when(unusedColorRole.getColor()).thenReturn(Color.of(0xE9, 0x1E, 0x63));
    when(unusedColorRole.delete(anyString())).thenReturn(Mono.empty());

    Role normalRole = mock(Role.class);
    when(normalRole.getName()).thenReturn("Admin");
    when(normalRole.getId()).thenReturn(Snowflake.of(333L));

    Member activeMember = mock(Member.class);
    when(activeMember.getRoleIds()).thenReturn(Set.of(Snowflake.of(111L), Snowflake.of(333L)));

    when(mockGuild.getMembers()).thenReturn(Flux.just(activeMember));
    when(mockGuild.getRoles()).thenReturn(Flux.just(usedColorRole, unusedColorRole, normalRole));
    when(mockGuild.getName()).thenReturn("Test Guild");

    Long deletedCount = colorService.cleanupOrphanColorRoles(mockGuild).block();

    assertThat(deletedCount).isEqualTo(1L);
    verify(unusedColorRole, times(1)).delete(anyString());
    verify(usedColorRole, never()).delete(anyString());
    verify(normalRole, never()).delete(anyString());
  }

  @Test
  void cleanupOrphanColorRoles_consolidatesDuplicateRolesAndMigratesMembers() {
    // Canonical role
    Role canonicalRole = mock(Role.class);
    when(canonicalRole.getName()).thenReturn("color-#1ABC9C");
    when(canonicalRole.getId()).thenReturn(Snowflake.of(111L));
    when(canonicalRole.getColor()).thenReturn(Color.of(0x1A, 0xBC, 0x9C));

    // Duplicate role with same RGB
    Role duplicateRole = mock(Role.class);
    when(duplicateRole.getName()).thenReturn("color-teal");
    when(duplicateRole.getId()).thenReturn(Snowflake.of(222L));
    when(duplicateRole.getColor()).thenReturn(Color.of(0x1A, 0xBC, 0x9C));
    when(duplicateRole.delete(anyString())).thenReturn(Mono.empty());

    // Member assigned to duplicate role
    Member userWithDup = mock(Member.class);
    when(userWithDup.getRoleIds()).thenReturn(Set.of(Snowflake.of(222L)));
    when(userWithDup.addRole(Snowflake.of(111L))).thenReturn(Mono.empty());
    when(userWithDup.removeRole(Snowflake.of(222L))).thenReturn(Mono.empty());

    // Member assigned to canonical role
    Member userWithCanonical = mock(Member.class);
    when(userWithCanonical.getRoleIds()).thenReturn(Set.of(Snowflake.of(111L)));

    when(mockGuild.getMembers()).thenReturn(Flux.just(userWithDup, userWithCanonical));
    when(mockGuild.getRoles()).thenReturn(Flux.just(canonicalRole, duplicateRole));

    Long deletedCount = colorService.cleanupOrphanColorRoles(mockGuild).block();

    assertThat(deletedCount).isEqualTo(1L);
    verify(userWithDup, times(1)).addRole(Snowflake.of(111L));
    verify(userWithDup, times(1)).removeRole(Snowflake.of(222L));
    verify(duplicateRole, times(1)).delete(anyString());
    verify(canonicalRole, never()).delete(anyString());
  }
}
