package io.github.bedwarsrevolution.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameOverTaskNew extends BukkitRunnable {
  private final GameContext ctx;
  private int counter = 10;
  private int counterStart = 10;
  private TeamNew winner = null;

  public GameOverTaskNew(GameContext ctx, int counter, TeamNew winner) {
    this.ctx = ctx;
    this.counterStart = counter;
    this.counter = counter;
    this.winner = winner;
  }

  @Override
  public void run() {
    if (this.counter == this.counterStart && this.winner != null) {
      for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(
              ChatWriterNew.pluginMessage(ChatColor.GOLD + BedwarsRevol._l(aPlayer, "ingame.teamwon",
                  ImmutableMap.of("team", this.winner.getDisplayName() + ChatColor.GOLD))));
        }
      }
      this.ctx.stopWorkers();
    } else if (this.counter == this.counterStart && this.winner == null) {
      for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(
              ChatWriterNew.pluginMessage(ChatColor.GOLD + BedwarsRevol._l(aPlayer, "ingame.draw")));
        }
      }
    }

    if (this.ctx.getPlayers().size() == 0 || this.counter == 0) {
//      BedwarsGameEndEvent endEvent = new BedwarsGameEndEvent(this.getGame());
//      BedwarsRel.getInstance().getServer().getPluginManager().callEvent(endEvent);

      this.onGameEnds();
      this.cancel();
    } else {
      for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
        Player aPlayer = aPlayerCtx.getPlayer();
        if (aPlayer.isOnline()) {
          aPlayer.sendMessage(
              ChatWriterNew.pluginMessage(
                  ChatColor.AQUA + BedwarsRevol
                      ._l(aPlayer, "ingame.backtolobby", ImmutableMap.of("sec",
                          ChatColor.YELLOW.toString() + this.counter + ChatColor.AQUA))));
        }
      }
    }

    this.counter--;
  }

  public void onGameEnds() {
    for (PlayerContext aPlayerCtx : this.ctx.getPlayers()) {
      this.ctx.getState().playerLeaves(aPlayerCtx, false);
    }
    this.ctx.reset();

//    // Reset scoreboard first
//    this.ctx.resetScoreboard();
//
//    // First team players, they get a reserved slot in lobby
//    for (Player p : this.getGame().getTeamPlayers()) {
//      this.kickPlayer(p, false);
//    }
//
//    // and now the spectators
//    List<Player> freePlayers = new ArrayList<Player>(this.getGame().getFreePlayers());
//    for (Player p : freePlayers) {
//      this.kickPlayer(p, true);
//    }
//
//    // reset countdown prevention breaks
//    this.setEndGameRunning(false);
//
//    // Reset teams
//    for (Team team : this.getGame().getTeams().values()) {
//      team.reset();
//    }
//
//    // clear protections
//    this.getGame().clearProtections();
//
//    // reset region
//    this.getGame().resetRegion();
//
//    // Restart lobby directly?
//    if (this.getGame().isStartable() && this.getGame().getLobbyCountdown() == null) {
//      GameLobbyCountdown lobbyCountdown = new GameLobbyCountdown(this.getGame());
//      lobbyCountdown.runTaskTimer(BedwarsRel.getInstance(), 20L, 20L);
//      this.getGame().setLobbyCountdown(lobbyCountdown);
//    }
//
//    // set state and with that, the sign
//    this.getGame().setState(GameStateOld.WAITING);
//    this.getGame().updateScoreboard();
  }

}
