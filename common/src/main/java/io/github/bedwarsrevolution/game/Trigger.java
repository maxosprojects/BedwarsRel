package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.Location;

/**
 * Created by {maxos} 2017
 */
public abstract class Trigger {
  private TeamNew owner;

  public Trigger(TeamNew owner) {
    this.owner = owner;
  }

  public boolean shouldTrigger(TeamNew team) {
    return this.owner != team;
  }

  public abstract void activate(PlayerContext playerCtx, Location loc);

}
