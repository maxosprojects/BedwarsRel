package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Upgrade {
  public abstract UpgradeScope getScope();

  public abstract UpgradeCycle getCycle();

  public abstract UpgradeScope getApplyTo();

  public abstract boolean isLevel(int level);

  /**
   * Builds and returns a new unactivated @{@link Upgrade}.
   * Unactivated means: not yet initialized, nor added to the game
   *
   * @param gameContext
   * @param team
   * @param playerCtx
   * @return
   */
  public abstract Upgrade build(GameContext gameContext, TeamNew team, PlayerContext playerCtx);

  /**
   * Activates (initializes) the item and adds it to the game.
   *
   * @return whether item initialization was successful (e.g. was successfully added to the game).
   */
  public abstract boolean activate(UpgradeScope scope, UpgradeCycle cycle);

  /**
   * Is called when an item is used (e.g. egg is thrown or landmine glasses are used)
   *
   * @return whether the item has been processed and and no further processing should be done for the item
   */
  public boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    return false;
  }

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

  public abstract boolean isMaterial(Material type);
}
