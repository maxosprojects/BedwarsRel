package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Created by {maxos} 2017
 */
public class UpgradeItemFireball extends UpgradeItem {
  protected final String TYPE = "FIREBALL_ITEM";

  @Override
  public boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    if (item.getType() == Material.FIREBALL) {
      if (!playerCtx.useItem(Material.FIREBALL.name())) {
        return true;
      }
      this.launchFireball(playerCtx.getPlayer());
      return true;
    }
    return false;
  }

  private void launchFireball(Player player) {
    // Take one fireball from the player
    Inventory inv = player.getInventory();
    int slot = inv.first(Material.FIREBALL);
    ItemStack stack = inv.getItem(slot);
    stack.setAmount(stack.getAmount() - 1);
    // Launch fireball
    Vector vec = player.getLocation().getDirection();
    Fireball ball = player.launchProjectile(Fireball.class);
    // Setting incendiary to false disables explosion damage oO
//      ball.setIsIncendiary(false);
//      ball.setYield(2);
    ball.setVelocity(vec);
  }
}
