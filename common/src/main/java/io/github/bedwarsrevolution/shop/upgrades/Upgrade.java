package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;

public abstract class Upgrade {
  public abstract UpgradeScope getScope();

  public abstract UpgradeCycle getCycle();

  public abstract UpgradeScope getApplyTo();

  public abstract boolean isLevel(int level);

  /**
   * Creates and returns a new uninitialized (not yet added to the game) @{@link Upgrade}
   *
   * @param gameContext
   * @param team
   * @param playerCtx
   * @return
   */
  public abstract Upgrade create(GameContext gameContext, TeamNew team, PlayerContext playerCtx);

  /**
   * Initializes the item and adds it to the game.
   *
   * @return whether item initialization was successful (e.g. was successfully added to the game).
   */
  public abstract boolean activate(UpgradeScope scope, UpgradeCycle cycle);

  public abstract String getType();

  public abstract void setPermanent(boolean permanent);

  public abstract boolean isPermanent();

  public abstract void setMultiple(boolean multiple);

  public boolean shouldRender(PlayerContext playerCtx) {
    return true;
  }

  public boolean alreadyOwn(PlayerContext playerCtx) {
    return false;
  };
}
