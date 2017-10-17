package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
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
public abstract class GameState {
  protected GameContext ctx;

  public GameState(GameContext ctx) {
    this.ctx = ctx;
  }

  public abstract void onEventCraft(CraftItemEvent event);

  public abstract void onEventDamage(EntityDamageEvent event);

  public abstract void onEventDrop(PlayerDropItemEvent event);

  public abstract void onEventFly(PlayerToggleFlightEvent event);

  public abstract void onEventBowShot(EntityShootBowEvent event);

  public abstract void onEventInteractEntity(PlayerInteractEntityEvent event);

  public abstract void onEventInventoryClick(InventoryClickEvent event);

  public abstract void onEventPlayerInteract(PlayerInteractEvent event);

  public abstract void onEventPlayerRespawn(PlayerRespawnEvent event);

  public abstract void onEventPlayerQuit(PlayerQuitEvent event);

  public abstract void onEventPlayerBedEnter(PlayerBedEnterEvent event);

  public abstract void onEventPlayerChangeWorld(PlayerChangedWorldEvent event);

  public abstract void onEventInventoryOpen(InventoryOpenEvent event);

  public abstract void playerJoins(Player player);

  public abstract void playerLeaves(PlayerContext playerCtx, boolean kicked);

  public void onEventBlockBreak(BlockBreakEvent event) {};

  public void onEventBlockBurn(BlockBurnEvent event) {
    event.setCancelled(true);
  }

  public void onEventBlockFade(BlockFadeEvent event) {
    if (!this.ctx.getRegion().isPlacedBlock(event.getBlock())) {
      event.setCancelled(true);
    }
  }

  public void onEventBlockForm(BlockFormEvent event) {
    event.setCancelled(true);
  }

  public abstract void onEventBlockIgnite(BlockIgniteEvent event);

  public abstract void onEventBlockPlace(BlockPlaceEvent event);

  public void onEventBlockSpread(BlockSpreadEvent event) {
  }

}
