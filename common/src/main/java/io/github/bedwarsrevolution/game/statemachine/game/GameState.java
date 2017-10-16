package io.github.bedwarsrevolution.game.statemachine.game;

import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
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
public interface GameState {

  void onEventCraft(GameContext ctx, CraftItemEvent event);

  void onEventDamage(GameContext ctx, EntityDamageEvent event);

  void onEventDrop(GameContext ctx, PlayerDropItemEvent event);

  void onEventFly(GameContext ctx, PlayerToggleFlightEvent event);

  void onEventBowShot(GameContext ctx, EntityShootBowEvent event);

  void onEventInteractEntity(GameContext ctx, PlayerInteractEntityEvent event);

  void onEventInventoryClick(GameContext ctx, InventoryClickEvent event);

  void onEventPlayerInteract(GameContext ctx, PlayerInteractEvent event);

  void onEventPlayerRespawn(GameContext ctx, PlayerRespawnEvent event);

  void onEventPlayerQuit(GameContext ctx, PlayerQuitEvent event);

  void onEventPlayerBedEnter(GameContext ctx, PlayerBedEnterEvent event);

  void onEventPlayerChangeWorld(GameContext ctx, PlayerChangedWorldEvent event);

  void onEventInventoryOpen(GameContext ctx, InventoryOpenEvent event);

  void playerJoins(GameContext ctx, Player player);

  void playerLeaves(GameContext ctx, PlayerContext playerCtx, boolean kicked);
}
