package io.github.bedwarsrevolution.game.statemachine.player;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.UtilsNew;
import org.bukkit.ChatColor;
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

  public abstract void onDeath();

  public abstract void onDamage(EntityDamageEvent event);

  public abstract void onDrop(PlayerDropItemEvent event);

  public abstract void onFly(PlayerToggleFlightEvent event);

  public abstract void onBowShot(EntityShootBowEvent event);

  public abstract void onInteractEntity(PlayerInteractEntityEvent event);

  public abstract void onInventoryClick(InventoryClickEvent event);

  public void leave(boolean kicked) {
    for (PlayerContext aPlayerCtx : this.playerCtx.getGameContext().getPlayers()) {
      Player aPlayer = aPlayerCtx.getPlayer();
      if (aPlayer.isOnline()) {
        String msgKey;
        if (kicked) {
          msgKey = "ingame.player.kicked";
        } else {
          msgKey = "ingame.player.left";
        }
        aPlayer.sendMessage(
            ChatWriterNew.pluginMessage(ChatColor.RED + BedwarsRevol
                ._l(aPlayer, msgKey, ImmutableMap.of("player",
                    UtilsNew.getPlayerWithTeamString(this.playerCtx.getPlayer(),
                        this.playerCtx.getTeam(), ChatColor.RED) + ChatColor.RED))));
      }
    }
  }

  public boolean isSpectator() {
    return false;
  }

  public abstract void setGameMode();
}
