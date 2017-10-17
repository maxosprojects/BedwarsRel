package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by {maxos} 2017
 */
public class GameStateEnding implements GameState {

  @Override
  public void onEventCraft(GameContext ctx, CraftItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventDamage(GameContext ctx, EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventDrop(GameContext ctx, PlayerDropItemEvent event) {
  }

  @Override
  public void onEventFly(GameContext ctx, PlayerToggleFlightEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onFly(playerCtx, event);
  }

  @Override
  public void onEventBowShot(GameContext ctx, EntityShootBowEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext((Player) event.getEntity());
    playerCtx.getState().onBowShot(playerCtx, event);
  }

  @Override
  public void onEventInteractEntity(GameContext ctx, PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventInventoryClick(GameContext ctx, InventoryClickEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerInteract(GameContext ctx, PlayerInteractEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerRespawn(GameContext ctx, PlayerRespawnEvent event) {
  }

  @Override
  public void onEventPlayerQuit(GameContext ctx, PlayerQuitEvent event) {
    this.playerLeaves(ctx, ctx.getPlayerContext(event.getPlayer()), false);
  }

  @Override
  public void onEventPlayerBedEnter(GameContext ctx, PlayerBedEnterEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerChangeWorld(GameContext ctx, PlayerChangedWorldEvent event) {
    PlayerContext playerCtx = ctx.getPlayerContext(event.getPlayer());
    if (!playerCtx.isTeleporting()) {
      this.playerLeaves(ctx, playerCtx, false);
    }
  }

  @Override
  public void onEventInventoryOpen(GameContext ctx, InventoryOpenEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void playerJoins(GameContext ctx, Player player) {
    player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
        ._l(player, "errors.cantjoingame")));
  }

  @Override
  public void playerLeaves(GameContext ctx, PlayerContext playerCtx, boolean kicked) {
    playerCtx.getState().leave(playerCtx, kicked);
    playerCtx.restoreLocation();
    playerCtx.restoreInventory();
    ctx.removePlayer(playerCtx);
  }

}
