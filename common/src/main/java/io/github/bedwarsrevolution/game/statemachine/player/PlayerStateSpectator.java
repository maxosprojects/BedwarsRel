package io.github.bedwarsrevolution.game.statemachine.player;

import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by {maxos} 2017
 */
public class PlayerStateSpectator implements PlayerState {

  @Override
  public void onDeath(PlayerContext playerCtx) {

  }

  @Override
  public void onDamage(PlayerContext playerCtx, EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onDrop(PlayerContext playerCtx, PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onFly(PlayerContext playerCtx, PlayerToggleFlightEvent event) {
    event.setCancelled(false);
  }

  @Override
  public void onBowShot(PlayerContext playerCtx, EntityShootBowEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInteractEntity(PlayerContext playerCtx, PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInventoryClick(PlayerContext playerCtx, InventoryClickEvent event) {
//    if (game.isSpectator(player)
//        || (game.getCycle() instanceof BungeeGameCycle && game.getCycle().isEndGameRunning()
//        && BedwarsRel.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))) {

    ItemStack clickedStack = event.getCurrentItem();
    if (clickedStack == null) {
      return;
    }

    event.setCancelled(true);

    if (!event.getInventory().getName().equals(
        BedwarsRevol._l(playerCtx.getPlayer(), "ingame.spectator"))) {
      return;
    }

    switch (clickedStack.getType()) {
      case SKULL_ITEM:
        SkullMeta meta = (SkullMeta) clickedStack.getItemMeta();
        Player teleportTo = BedwarsRevol.getInstance().getServer().getPlayer(meta.getOwner());
        if (playerCtx.getGameContext().getPlayerContext(teleportTo) == null) {
          return;
        }
        playerCtx.getPlayer().teleport(teleportTo);
        playerCtx.getPlayer().closeInventory();
        break;
      case SLIME_BALL:
        // TODO: implement what was in Game.playerLeave
        // playerCtx.leave(false);
      case COMPASS:
        // TODO: implement what was in game.openSpectatorCompass();
        // playerCtx.openSpectatorCompass(false);
    }
  }

}
