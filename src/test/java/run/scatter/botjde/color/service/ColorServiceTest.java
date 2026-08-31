package run.scatter.botjde.color.service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.RoleCreateSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.scatter.botjde.color.model.DiscordColorPalette.ResolvedColor;

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
    when(mockGuild.getRoles()).thenReturn(Flux.just(existingRole));
    when(mockMember.addRole(Snowflake.of(111L))).thenReturn(Mono.empty());

    ResolvedColor result = colorService.setColor(mockMember, "teal").block();

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
    when(mockGuild.createRole(any(RoleCreateSpec.class))).thenReturn(Mono.just(newRole));
    when(mockMember.addRole(Snowflake.of(222L))).thenReturn(Mono.empty());

    ResolvedColor result = colorService.setColor(mockMember, "#FF5733").block();

    assertThat(result).isNotNull();
    assertThat(result.hex()).isEqualTo("#FF5733");
    verify(mockGuild, times(1)).createRole(any(RoleCreateSpec.class));
    verify(mockMember, times(1)).addRole(Snowflake.of(222L));
  }

  @Test
  void removeColor_removesAllColorRoles() {
    Role colorRole1 = mock(Role.class);
    when(colorRole1.getName()).thenReturn("color-#1ABC9C");
    when(colorRole1.getId()).thenReturn(Snowflake.of(111L));

    Role normalRole = mock(Role.class);
    when(normalRole.getName()).thenReturn("Member");
    when(normalRole.getId()).thenReturn(Snowflake.of(999L));

    when(mockMember.getRoles()).thenReturn(Flux.just(colorRole1, normalRole));
    when(mockMember.removeRole(Snowflake.of(111L))).thenReturn(Mono.empty());

    colorService.removeColor(mockMember).block();

    verify(mockMember, times(1)).removeRole(Snowflake.of(111L));
    verify(mockMember, never()).removeRole(Snowflake.of(999L));
  }
}
