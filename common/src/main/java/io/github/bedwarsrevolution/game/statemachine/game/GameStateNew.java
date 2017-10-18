package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Created by {maxos} 2017
 */
public abstract class GameStateNew {
  protected GameContext ctx;

  public GameStateNew(GameContext ctx) {
    this.ctx = ctx;
  }

  public abstract void onEventCraft(CraftItemEvent event);

  public abstract void onEventEntityDamage(EntityDamageEvent event);

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

  public void onEventEntityPickupItem(EntityPickupItemEvent event) {
  }

  public void onEventPlayerPickupItem(PlayerPickupItemEvent event) {
  }

  public abstract void onEventPlayerSwapHandItems(PlayerSwapHandItemsEvent event);

  public void onEventChunkUnload(ChunkUnloadEvent event) {
  }

  public void onEventCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getEntityType().equals(EntityType.CREEPER)
        || event.getEntityType().equals(EntityType.CAVE_SPIDER)
        || event.getEntityType().equals(EntityType.SPIDER)
        || event.getEntityType().equals(EntityType.ZOMBIE)
        || event.getEntityType().equals(EntityType.SKELETON)
        || event.getEntityType().equals(EntityType.SILVERFISH)) {
      event.setCancelled(true);
    }
  }

  public abstract void onEventEntityExplode(EntityExplodeEvent event);

  public void onEventRegainHealth(EntityRegainHealthEvent event) {
  }

  public abstract void onEventServerListPing(ServerListPingEvent event);

  protected String motdReplacePlaceholder(String line) {
    String finalLine = line;
    finalLine = finalLine.replace("$title$", BedwarsRevol._l("sign.firstline"));
    finalLine = finalLine.replace("$gamename$", this.ctx.getName());
    if (this.ctx.getRegion().getName() != null) {
      finalLine = finalLine.replace("$regionname$", this.ctx.getRegion().getName());
    } else {
      finalLine = finalLine.replace("$regionname$", this.ctx.getName());
    }
    String maxPlayers = String.valueOf(this.ctx.getMaxPlayers());
    finalLine = finalLine.replace("$maxplayers$", maxPlayers);
    String currentPlayers = String.valueOf(this.ctx.getPlayers().size());
    finalLine = finalLine.replace("$currentplayers$", currentPlayers);
    finalLine = finalLine.replace("$status$", this.getClass().getName());

    return finalLine;
  }

  public void onEventWeatherChange(WeatherChangeEvent event) {
    event.setCancelled(true);
  }

  public String getStatus() {
    return BedwarsRevol._l("sign.gamestate." + this.getStatusKey());
  }

  protected abstract String getStatusKey();

  public void updateTime() {
  }

  public void onEventPlayerMove(PlayerMoveEvent event) {
  }

//  private String getStatus(Game game) {
//    String status = null;
//    if (game.getState() == GameState.WAITING && game.isFull()) {
//      status = ChatColor.RED + BedwarsRel._l("sign.gamestate.full");
//    } else {
//      status = BedwarsRel._l("sign.gamestate." + game.getState().toString().toLowerCase());
//    }
//
//    return status;
//  }


}
