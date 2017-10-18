package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class UpgradeBaseAlarmListener implements Listener {

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    this.processMove(event);
  }

  @EventHandler
  public void onTeleport(PlayerTeleportEvent event) {
    this.processMove(event);
  }

  private void processMove(PlayerMoveEvent event) {
    if (event.isCancelled()) {
      return;
    }
    double difX = Math.abs(event.getFrom().getX() - event.getTo().getX());
    double difZ = Math.abs(event.getFrom().getZ() - event.getTo().getZ());
    if (difX == 0.0 && difZ == 0.0) {
      return;
    }
    Player player = event.getPlayer();
    GameContext gameCtx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (gameCtx == null) {
      return;
    }
    gameCtx.getState().onEventPlayerMove(event);
  }

}
