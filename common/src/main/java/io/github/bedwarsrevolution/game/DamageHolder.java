package io.github.bedwarsrevolution.game;

import lombok.Getter;
import org.bukkit.entity.Player;

public class DamageHolder {
  // Interval of time the damage is considered recent
  private static long RECENT_INTERVAL = 5000;

  @Getter
  private final Player damager;
  @Getter
  private final long happenedAt;

  public DamageHolder(Player damager) {
    this.damager = damager;
    this.happenedAt = System.currentTimeMillis();
  }

  public boolean wasCausedRecently() {
    return (happenedAt + RECENT_INTERVAL) > System.currentTimeMillis();
  }

}
