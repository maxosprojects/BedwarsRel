package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriter;
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
public class GameStateStopped implements GameState {

  @Override
  public void onEventCraft(GameContext ctx, CraftItemEvent event) {

  }

  @Override
  public void onEventDamage(GameContext ctx, EntityDamageEvent event) {

  }

  @Override
  public void onEventDrop(GameContext ctx, PlayerDropItemEvent event) {

  }

  @Override
  public void onEventFly(GameContext ctx, PlayerToggleFlightEvent event) {

  }

  @Override
  public void onEventBowShot(GameContext ctx, EntityShootBowEvent event) {

  }

  @Override
  public void onEventInteractEntity(GameContext ctx, PlayerInteractEntityEvent event) {

  }

  @Override
  public void onEventInventoryClick(GameContext ctx, InventoryClickEvent event) {

  }

  @Override
  public void onEventPlayerInteract(GameContext ctx, PlayerInteractEvent event) {
  }

  @Override
  public void onEventPlayerRespawn(GameContext ctx, PlayerRespawnEvent event) {

  }

  @Override
  public void onEventPlayerQuit(GameContext ctx, PlayerQuitEvent event) {

  }

  @Override
  public void onEventPlayerBedEnter(GameContext ctx, PlayerBedEnterEvent event) {

  }

  @Override
  public void onEventPlayerChangeWorld(GameContext ctx, PlayerChangedWorldEvent event) {

  }

  @Override
  public void onEventInventoryOpen(GameContext ctx, InventoryOpenEvent event) {

  }

  @Override
  public void playerJoins(GameContext ctx, Player player) {
//    if (this.cycle instanceof BungeeGameCycle) {
//      ((BungeeGameCycle) this.cycle).sendBungeeMessage(p,
//          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(p, "errors.cantjoingame")));
//    } else {
      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRevol
          ._l(player, "errors.cantjoingame")));
//    }
  }

}
