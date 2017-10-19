package io.github.bedwarsrevolution.game.statemachine.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.game.GameStateRunning;
import io.github.bedwarsrevolution.game.statemachine.game.GameStateWaiting;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import io.github.bedwarsrevolution.utils.SoundMachineNew;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameLobbyCountdownNew extends BukkitRunnable {

  private final GameContext ctx;
  @Getter
  @Setter
  private int counter = 0;
  @Getter
  private int lobbytime;
  @Getter
  private int lobbytimeWhenFull;

  public GameLobbyCountdownNew(GameContext ctx) {
    this.ctx = ctx;
    this.counter = BedwarsRevol.getInstance().getConfig().getInt("lobbytime");
    this.lobbytime = this.counter;
    this.lobbytimeWhenFull = BedwarsRevol.getInstance().getConfig().getInt("lobbytime-full");
  }

  @Override
  public void run() {
    Collection<PlayerContext> playersContexts = this.ctx.getPlayers();
    float xpPerLevel = 1.0F / this.lobbytime;

    if (this.counter > this.lobbytimeWhenFull
        && playersContexts.size() == this.ctx.getMaxPlayers()) {
      this.counter = this.lobbytimeWhenFull;
      for (PlayerContext playerCtx : playersContexts) {
        Player player = playerCtx.getPlayer();
        if (player.isOnline()) {
          player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.YELLOW
              + BedwarsRevol._l(player, "lobby.countdown", ImmutableMap.of("sec",
                      ChatColor.RED.toString() + this.counter + ChatColor.YELLOW))));
        }
      }
    }

//    if (this.counter == this.lobbytimeWhenFull) {
//      for (PlayerContext playerCtx : playersContexts) {
//        Player player = playerCtx.getPlayer();
//        if (player.getInventory().contains(Material.EMERALD)) {
//          player.getInventory().remove(Material.EMERALD);
//        }
//      }
//    }

    for (PlayerContext playerCtx : playersContexts) {
      Player player = playerCtx.getPlayer();
      player.setLevel(this.counter);
      if (this.counter == this.lobbytime) {
        player.setExp(1.0F);
      } else {
        player.setExp(1.0F - (xpPerLevel * (this.lobbytime - this.counter)));
      }

    }

    if (this.counter == this.lobbytime) {
      for (PlayerContext playerCtx : playersContexts) {
        Player player = playerCtx.getPlayer();
        if (player.isOnline()) {
          player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.YELLOW
              + BedwarsRevol._l(player, "lobby.countdown", ImmutableMap.of("sec",
                      ChatColor.RED.toString() + this.counter + ChatColor.YELLOW))));
        }
      }

//      for (Player p : players) {
//        if (!p.getInventory().contains(Material.DIAMOND) && p.hasPermission("bw.vip.forcestart")) {
//          this.game.getPlayerStorage(p).addGameStartItem();
//        }
//
//        if (!p.getInventory().contains(Material.EMERALD) && (p.isOp() || p.hasPermission("bw.setup")
//            || p.hasPermission("bw.vip.reducecountdown"))) {
//          this.game.getPlayerStorage(p).addReduceCountdownItem();
//        }
//      }
    }

//    if (!this.game.isStartable()) {
//      if (!this.game.hasEnoughPlayers()) {
//        for (Player aPlayer : players) {
//          if (aPlayer.isOnline()) {
//            aPlayer.sendMessage(ChatWriter.pluginMessage(
//                ChatColor.RED + BedwarsRel
//                    ._l(aPlayer, "lobby.cancelcountdown.not_enough_players")));
//          }
//        }
//      } else if (!this.game.hasEnoughTeams()) {
//        for (Player aPlayer : players) {
//          if (aPlayer.isOnline()) {
//            aPlayer.sendMessage(ChatWriter.pluginMessage(
//                ChatColor.RED + BedwarsRel._l(aPlayer, "lobby.cancelcountdown.not_enough_teams")));
//          }
//        }
//      }
//
//      this.counter = this.lobbytime;
//      for (Player p : players) {
//        p.setLevel(0);
//        p.setExp(0.0F);
//        if (p.getInventory().contains(Material.EMERALD)) {
//          p.getInventory().remove(Material.EMERALD);
//        }
//      }
//
//      this.game.setGameLobbyCountdown(null);
//      this.cancel();
//    }

    if (this.counter <= 10 && this.counter > 0) {
      for (PlayerContext playerCtx : playersContexts) {
        Player player = playerCtx.getPlayer();
        if (player.isOnline()) {
          player.sendMessage(ChatWriterNew.pluginMessage(ChatColor.YELLOW
              + BedwarsRevol._l(player, "lobby.countdown", ImmutableMap.of("sec",
                      ChatColor.RED.toString() + this.counter + ChatColor.YELLOW))));
        }
      }

//      Class<?> titleClass = null;
//      Method showTitle = null;
      String title = ChatColor.translateAlternateColorCodes('&',
          BedwarsRevol.getInstance().getStringConfig("titles.countdown.format", "&3{countdown}"));
      title = title.replace("{countdown}", String.valueOf(this.counter));

//      if (BedwarsRevol.getInstance().getBooleanConfig("titles.countdown.enabled", true)) {
//        try {
//          titleClass = BedwarsRel.getInstance().getVersionRelatedClass("Title");
//          showTitle = titleClass.getMethod("showTitle", Player.class, String.class, double.class,
//              double.class, double.class);
//        } catch (Exception ex) {
//          BedwarsRel.getInstance().getBugsnag().notify(ex);
//          ex.printStackTrace();
//        }
//      }

      for (PlayerContext playerCtx : playersContexts) {
        Player player = playerCtx.getPlayer();
        player.playSound(player.getLocation(), SoundMachineNew.get("CLICK", "UI_BUTTON_CLICK"),
            Float.valueOf("1.0"), Float.valueOf("1.0"));
        player.sendTitle(title, "", 0, 10, 10);
//        if (titleClass == null) {
//          continue;
//        }
//
//        try {
//          showTitle.invoke(null, player, title, 0.2, 0.6, 0.2);
//        } catch (Exception ex) {
//          BedwarsRel.getInstance().getBugsnag().notify(ex);
//          ex.printStackTrace();
//        }
      }
    }

    if (this.counter == 0) {
//      this.game.setGameLobbyCountdown(null);
      this.cancel();
      for (PlayerContext playerCtx : playersContexts) {
        Player player = playerCtx.getPlayer();
        player.playSound(player.getLocation(),
            SoundMachineNew.get("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), Float.valueOf("1.0"),
            Float.valueOf("1.0"));
        player.setLevel(0);
        player.setExp(0.0F);
      }

      GameStateRunning newState = new GameStateRunning(this.ctx);
      this.ctx.setState(newState);
      newState.startGame();
      return;
    }

    this.counter--;
  }
}
