package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.listeners.BaseListenerNew;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickUpItemEventListenerNew extends BaseListenerNew {

  @EventHandler
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventPlayerPickupItem(event);
  }

}
