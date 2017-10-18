package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;

public interface Upgrade {
  UpgradeScope getScope();

  UpgradeCycle getCycle();

  UpgradeScope getApplyTo();

  boolean isLevel(int level);

  /**
   * Creates and returns a new uninitialized (not yet added to the game) @{@link Upgrade}
   *
   * @param gameContext
   * @param team
   * @param playerCtx
   * @return
   */
  Upgrade create(GameContext gameContext, TeamNew team, PlayerContext playerCtx);

  /**
   * Initializes the item and adds it to the game.
   *
   * @return whether item initialization was successful (e.g. was successfully added to the game).
   */
  boolean activate(UpgradeScope scope, UpgradeCycle cycle);

  String getType();

  void setPermanent(boolean permanent);

  boolean isPermanent();

  void setMultiple(boolean multiple);

}
