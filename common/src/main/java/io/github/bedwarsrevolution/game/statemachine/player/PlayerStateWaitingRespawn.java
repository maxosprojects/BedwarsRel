package io.github.bedwarsrevolution.game.statemachine.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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

  public PlayerStateWaitingRespawn(PlayerContext playerCtx) {
    super(playerCtx);
  }

  @Override
  public void onDeath() {

  }

  @Override
  public void onDamage(EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onDrop(PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onFly(PlayerToggleFlightEvent event) {
    event.setCancelled(false);
  }

  @Override
  public void onBowShot(EntityShootBowEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInteractEntity(PlayerInteractEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onInventoryClick(InventoryClickEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void setGameMode() {
    Player player = this.playerCtx.getPlayer();
    player.setAllowFlight(true);
    player.setFlying(true);
    player.setGameMode(GameMode.SPECTATOR);
  }

}
