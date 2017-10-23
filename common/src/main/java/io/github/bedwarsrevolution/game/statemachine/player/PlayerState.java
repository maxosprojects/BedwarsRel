package io.github.bedwarsrevolution.game.statemachine.player;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import org.bukkit.Location;
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
public abstract class PlayerState {
  protected final PlayerContext playerCtx;

  public PlayerState(PlayerContext playerCtx) {
    this.playerCtx = playerCtx;
  }

  public abstract void onDeath(boolean byVoid);

  /**
   * Called only when the damaged entity is is the one in context
   * @param event
   * @param damager is only null if damage was caused to the player in context and damager
   *        couldn't be established, see
   *        {@link io.github.bedwarsrevolution.listeners.PlayerListenerNew#onEntityDamage(EntityDamageEvent)}
   * @return whether a game changer event happened (e.g. death or player left)
   */
  public abstract boolean onDamageToPlayer(EntityDamageEvent event, Player damager);

  /**
   * Called only when the damage was caused to a non-player entity and the damager is
   * the player in context
   * @param event
   * @return whether a game changer event happened (e.g. death or player left)
   */
  public abstract boolean onDamageByPlayer(EntityDamageEvent event);

  public abstract void onDropItem(PlayerDropItemEvent event);

  public abstract void onFly(PlayerToggleFlightEvent event);

  public abstract void onBowShot(EntityShootBowEvent event);

  public abstract void onInteractEntity(PlayerInteractEntityEvent event);

  public abstract void onInventoryClick(InventoryClickEvent event);

  public void leave(boolean kicked) {
    for (PlayerContext aPlayerCtx : this.playerCtx.getGameContext().getPlayers()) {
      Player aPlayer = aPlayerCtx.getPlayer();
      if (aPlayer != this.playerCtx && aPlayer.isOnline()) {
        String msgKey;
        if (kicked) {
          msgKey = "ingame.player.kicked";
        } else {
          msgKey = "ingame.player.left";
        }
        aPlayer.sendMessage(
            ChatWriterNew.pluginMessage(BedwarsRevol._l(aPlayer, msgKey,
                ImmutableMap.of("player",
                    UtilsNew.getPlayerWithTeamString(this.playerCtx)))));
      }
    }

  }

  public boolean isSpectator() {
    return false;
  }

  public abstract void setGameMode();

  void moveToTopCenter() {
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
  }

}
