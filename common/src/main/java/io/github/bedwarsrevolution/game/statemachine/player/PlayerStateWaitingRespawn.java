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
public class PlayerStateWaitingRespawn extends PlayerState {

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
    event.setCancelled(true);
  }

}
