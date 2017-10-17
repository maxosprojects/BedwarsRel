package io.github.bedwarsrevolution.shop.upgrades;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.game.GameStateRunning;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
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

  public void processMove(PlayerMoveEvent move) {
    if (move.isCancelled()) {
      return;
    }
    double difX = Math.abs(move.getFrom().getX() - move.getTo().getX());
    double difZ = Math.abs(move.getFrom().getZ() - move.getTo().getZ());
    if (difX == 0.0 && difZ == 0.0) {
      return;
    }
    Player player = move.getPlayer();
    GameContext gameCtx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (gameCtx == null) {
      return;
    }
    if (!(gameCtx.getState() instanceof GameStateRunning)) {
      return;
    }
    PlayerContext playerCtx = gameCtx.getPlayerContext(player);
    if (playerCtx.getState().isSpectator()) {
      return;
    }
    TeamNew team = playerCtx.getTeam();
    for (TeamNew otherTeam : gameCtx.getTeams().values()) {
      if (otherTeam == team) {
        continue;
      }
      UpgradeBaseAlarm alarm = otherTeam.getUpgrade(UpgradeBaseAlarm.class);
      if (alarm != null) {
        if (alarm.isLocationIn(move.getTo())) {
          alarm.trigger(player);
        }
      }
    }
  }

}
