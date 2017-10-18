package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by {maxos} 2017
 */
public class GameStateEnding extends GameState {
  private static final String TRANSLATION = "running";

  public GameStateEnding(GameContext ctx) {
    super(ctx);
  }

  @Override
  public void onEventCraft(CraftItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventEntityDamage(EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventDrop(PlayerDropItemEvent event) {
  }

  @Override
  public void onEventFly(PlayerToggleFlightEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    playerCtx.getState().onFly(playerCtx, event);
  }

  @Override
  public void onEventBowShot(EntityShootBowEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext((Player) event.getEntity());
    playerCtx.getState().onBowShot(playerCtx, event);
  }

  @Override
  public void onEventInteractEntity(PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventInventoryClick(InventoryClickEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerInteract(PlayerInteractEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerRespawn(PlayerRespawnEvent event) {
  }

  @Override
  public void onEventPlayerQuit(PlayerQuitEvent event) {
    this.playerLeaves(this.ctx.getPlayerContext(event.getPlayer()), false);
  }

  @Override
  public void onEventPlayerBedEnter(PlayerBedEnterEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerChangeWorld(PlayerChangedWorldEvent event) {
    PlayerContext playerCtx = this.ctx.getPlayerContext(event.getPlayer());
    if (!playerCtx.isTeleporting()) {
      this.playerLeaves(playerCtx, false);
    }
  }

  @Override
  public void onEventInventoryOpen(InventoryOpenEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void playerJoins(Player player) {
    player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
        ._l(player, "errors.cantjoingame")));
  }

  @Override
  public void playerLeaves(PlayerContext playerCtx, boolean kicked) {
    playerCtx.getState().leave(playerCtx, kicked);
    playerCtx.restoreLocation();
    playerCtx.restoreInventory();
    this.ctx.removePlayer(playerCtx);
  }

  @Override
  public void onEventBlockBreak(BlockBreakEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventBlockIgnite(BlockIgniteEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventBlockPlace(BlockPlaceEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onEventEntityExplode(EntityExplodeEvent event) {
    event.setYield(0);
  }

  @Override
  public void onEventServerListPing(ServerListPingEvent event) {
    event.setMotd(motdReplacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("bungeecord.motds.running"))));
  }

  @Override
  public String getTranslation() {
    return TRANSLATION;
  }

}
