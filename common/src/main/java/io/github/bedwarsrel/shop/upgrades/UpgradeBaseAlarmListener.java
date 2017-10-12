package io.github.bedwarsrel.shop.upgrades;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.shop.Specials.SpecialItem;
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
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    if (game.isSpectator(player)) {
      return;
    }

    Team team = game.getPlayerTeam(player);
    if (team == null || game.isSpectator(player)) {
      return;
    }

    for (Team otherTeam : game.getTeams().values()) {
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
