package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.listeners.BaseListenerNew;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemEventListenerNew extends BaseListenerNew {

  @EventHandler
  public void onEntityPickupItem(EntityPickupItemEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventEntityPickupItem(event);
  }

}
