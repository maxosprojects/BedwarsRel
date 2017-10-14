package io.github.bedwarsrel.game;

import io.github.bedwarsrel.BedwarsRel;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class PlayerFlags {
  private Object hologram = null;
  private boolean teleporting = false;
  private boolean oneStackPerShift = false;
  private Player player = null;
  private boolean virtuallyAlive = true;

  public PlayerFlags(Player player) {
    this.player = player;
    this.oneStackPerShift = BedwarsRel.getInstance()
        .getBooleanConfig("player-settings.one-stack-on-shift", true);
  }

}