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

  public abstract void onDeath(PlayerContext playerCtx);

  public abstract void onDamage(PlayerContext playerCtx, EntityDamageEvent event);

  public abstract void onDrop(PlayerContext playerCtx, PlayerDropItemEvent event);

  public abstract void onFly(PlayerContext playerCtx, PlayerToggleFlightEvent event);

  public abstract void onBowShot(PlayerContext playerCtx, EntityShootBowEvent event);

  public abstract void onInteractEntity(PlayerContext playerCtx, PlayerInteractEntityEvent event);

  public abstract void onInventoryClick(PlayerContext playerCtx, InventoryClickEvent event);

  public void leave(PlayerContext playerCtx, boolean kicked) {
    for (PlayerContext aPlayerCtx : playerCtx.getGameContext().getPlayers()) {
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
                    UtilsNew.getPlayerWithTeamString(playerCtx.getPlayer(),
                        playerCtx.getTeam(), ChatColor.RED) + ChatColor.RED))));
      }
    }
  }

  public boolean isSpectator() {
    return false;
  }

}
