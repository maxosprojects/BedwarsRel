package io.github.bedwarsrevolution.game.statemachine.player;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by {maxos} 2017
 */
public interface PlayerState {

  void onDeath(PlayerContext playerCtx);

  void onDamage(PlayerContext playerCtx, EntityDamageEvent event);

  void onDrop(PlayerContext playerCtx, PlayerDropItemEvent event);

  void onFly(PlayerContext playerCtx, PlayerToggleFlightEvent event);

  void onBowShot(PlayerContext playerCtx, EntityShootBowEvent event);

  void onInteractEntity(PlayerContext playerCtx, PlayerInteractEntityEvent event);

  void onInventoryClick(PlayerContext playerCtx, InventoryClickEvent event);
}
