package io.github.bedwarsrel.shop.upgrades;

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

public class TntListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || !BedwarsRel.getInstance().getBooleanConfig("tnt-autoignite", false)) {
            return;
        }

        Player player = event.getPlayer();
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
            player.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.TNT, 1) });
            player.updateInventory();
            Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
            block.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
        }
    }

}
