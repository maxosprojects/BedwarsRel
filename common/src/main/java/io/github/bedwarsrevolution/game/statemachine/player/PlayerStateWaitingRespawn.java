package io.github.bedwarsrevolution.game.statemachine.player;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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

  public void runWaitingRespawn() {
    if (!this.playerCtx.isVirtuallyAlive()) {
      return;
    }

    this.playerCtx.clear(false);
    this.playerCtx.respawn();

    this.playerCtx.setVirtuallyAlive(false);

//    if (this.getState() == GameStateOld.RUNNING && this.isStopping()) {
//      String title = ChatColor.translateAlternateColorCodes('&',
//          BedwarsRel._l(player, "ingame.title.youdied"));
//      player.sendTitle(title, "", 0, 40, 10);
//    }
    this.setGameMode();
    Player player = this.playerCtx.getPlayer();
    Location location = this.playerCtx.getGameContext().getTopMiddle();
    this.playerCtx.setTeleportingIfWorldChange(location);
    player.teleport(location);
    this.playerCtx.setTeleporting(false);

    // Task with countdown till respawn
    BukkitTask task = new BukkitRunnable() {
      private int respawnIn = 5;
      @Override
      public void run() {
        if (this.respawnIn == 0) {
          this.cancel();
          PlayerStateWaitingRespawn.this.respawn();
          return;
        }
        Player player = PlayerStateWaitingRespawn.this.playerCtx.getPlayer();
        String title = ChatColor.translateAlternateColorCodes('&',
            BedwarsRevol._l(player, "ingame.title.youdied"));
        String subtitle = ChatColor.translateAlternateColorCodes('&',
            BedwarsRevol._l(player, "ingame.title.respawninseconds",
                ImmutableMap.of("time", Integer.toString(this.respawnIn))));
        player.sendTitle(title, subtitle, 0, 20, 10);
        this.respawnIn--;
      }
    }.runTaskTimer(BedwarsRevol.getInstance(), 0, 20);
    this.playerCtx.getGameContext().addRunningTask(task);
  }

  private void respawn() {
    Player player = this.playerCtx.getPlayer();
    String title = ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol._l(player, "ingame.title.respawn"));
    player.sendTitle(title, "", 0, 10, 10);
    Location location = this.playerCtx.getTeam().getSpawnLocation();
    this.playerCtx.setTeleportingIfWorldChange(location);
    player.teleport(location);
    this.playerCtx.setTeleporting(false);
    PlayerState newState = new PlayerStatePlaying(this.playerCtx);
    this.playerCtx.setState(newState);
    newState.setGameMode();
    this.playerCtx.setVirtuallyAlive(true);
  }

}
