package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.TitleWriterNew;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by {maxos} 2017
 */
public class UpgradeItemLandmineGoggles extends UpgradeItem {
  protected final String TYPE = "LANDMINE_GOGGLES_ITEM";

  @Override
  public boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    if (item.getType() == Material.DIAMOND_SPADE && item.getDurability() == 1) {
      this.equipLandmineGoggles(playerCtx, item);
      return true;
    }
    return false;
  }

  private void equipLandmineGoggles(final PlayerContext playerCtx, final ItemStack newHelmet) {
    final Player player = playerCtx.getPlayer();
    GameContext ctx = playerCtx.getGameContext();
    if (playerCtx.getHelmet() != null) {
      player.sendMessage(ChatWriterNew.pluginMessage("&cCurrently wearing goggles"));
      return;
    }
    BedwarsRevol.getInstance().getBlockDisguiser().addGogglesUser(playerCtx);
    playerCtx.setHelmet(newHelmet.clone());

    // Remove one goggles item from player's inventory
    ctx.addRunningTask(new BukkitRunnable() {
      public void run() {
        newHelmet.setAmount(newHelmet.getAmount() - 1);
      }
    }.runTaskLater(BedwarsRevol.getInstance(), 1L));

    final long wearingTime = System.currentTimeMillis();
    ctx.addRunningTask(new BukkitRunnable() {
      public void run() {
        // Make sure player is still in the game and hasn't died since goggles were put on
        PlayerContext currentPlayerCtx = BedwarsRevol.getInstance().getGameManager()
            .getGameOfPlayer(player).getPlayerContext(player);
        if (currentPlayerCtx != null && currentPlayerCtx.getLastDeath() <= wearingTime) {
          BedwarsRevol.getInstance().getBlockDisguiser().removeGogglesUser(playerCtx);
          currentPlayerCtx.restoreHelmet();
          player.sendTitle("", TitleWriterNew.pluginMessage("&cLandmine Goggles expired"), 10, 70, 20);
        }
      }
    }.runTaskLater(BedwarsRevol.getInstance(), 600L));
  }
}
