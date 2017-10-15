package io.github.bedwarsrevolution.shop.upgrades;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.game.ResourceSpawnerNew;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.HashMap;
import java.util.Map;

public enum UpgradeForgeEnum implements UpgradeEnum {
  FORGE0(new HashMap<String, Integer>()),
  FORGE1(ImmutableMap.of(
      "iron", 2000,
      "gold", 8000)),
  FORGE2(ImmutableMap.of(
      "iron", 1500,
      "gold", 6000)),
  FORGE3(ImmutableMap.of(
      "emerald_team", 50000)),
  FORGE4(ImmutableMap.of(
      "iron", 1000,
      "gold", 4000,
      "emerald_team", 16700));

  private final Map<String, Integer> intervals;

  UpgradeForgeEnum(Map<String, Integer> intervals) {
    this.intervals = intervals;
  }

  public boolean isHigherThan(UpgradeForgeEnum purchase) {
    return this.ordinal() > purchase.ordinal();
  }

  public void equipTeam(GameContext ctx, TeamNew team) {
    ctx.getResourceSpawnerManager().restart(this.intervals, team);
  }

}
