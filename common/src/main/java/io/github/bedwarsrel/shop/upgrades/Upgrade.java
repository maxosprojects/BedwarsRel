package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.entity.Player;

public interface Upgrade {
  UpgradeScope getScope();

  UpgradeCycle getCycle();

  UpgradeScope getApplyTo();

  boolean isLevel(int level);

  /**
   * Creates and returns a new uninitialized (not yet added to the game) @{@link Upgrade}
   *
   * @param game
   * @param team
   * @param player
   * @return
   */
  Upgrade create(Game game, Team team, Player player);

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
