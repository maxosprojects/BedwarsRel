package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpgradeSwordItem extends UpgradeItem {
  private static final String TYPE = "SWORD_ITEM";

  private int level;

  public UpgradeSwordItem(int level) {
    super();
    this.level = level;
  }

  @Override
  public Upgrade build(GameContext gameCtx, TeamNew team, PlayerContext playerCtx) {
    UpgradeSwordItem item = new UpgradeSwordItem(this.level);
    item.gameCtx = gameCtx;
    item.playerCtx = playerCtx;
    item.purchase = this.purchase;
    item.permanent = this.permanent;
    item.multiple = this.multiple;
    return item;
  }

  @Override
  public boolean activate(UpgradeScope scope, UpgradeCycle cycle) {
    // Replace existing WOOD_SWORD
    if (this.purchase.getType() == Material.WOOD_SWORD) {
      this.installItem();
    } else {
      Player player = this.playerCtx.getPlayer();
      Inventory inv = player.getInventory();
      ItemStack item = this.purchase.clone();
      int replaceSlot = inv.first(Material.WOOD_SWORD);
      if (replaceSlot == -1) {
        inv.addItem(item);
      } else {
        inv.setItem(replaceSlot, item);
      }
      player.updateInventory();
    }
    this.msg(cycle, true);

    TeamNew team = this.playerCtx.getTeam();
    UpgradeSwordSharpness sharpness = team.getUpgrade(UpgradeSwordSharpness.class);
    if (sharpness != null) {
      sharpness.getUpgrade().equipPlayer(this.playerCtx);
    }
    return true;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public boolean isLevel(int lev) {
    return this.level == lev;
  }

}
