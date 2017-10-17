package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class TntListenerNew implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlace(BlockPlaceEvent event) {
    if (event.isCancelled() || !BedwarsRel.getInstance()
        .getBooleanConfig("tnt-autoignite", false)) {
      return;
    }

    final Player player = event.getPlayer();
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    Team team = game.getPlayerTeam(player);
    if (team == null) {
      return;
    }

    Block block = event.getBlock();
    if (block.getType() == Material.TNT) {
      event.setCancelled(true);
      Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
      block.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
      new BukkitRunnable() {
        public void run() {
//          player.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.TNT, 1)});
//          player.updateInventory();
          PlayerInventory inv = player.getInventory();
          int slot = inv.getHeldItemSlot();
          ItemStack stack = inv.getItem(slot);
          stack.setAmount(stack.getAmount() - 1);
        }
      }.runTaskLater(BedwarsRel.getInstance(), 1L);
    }
  }

}
