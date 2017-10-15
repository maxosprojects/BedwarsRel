package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * Created by {maxos} 2017
 */
public class GameScoreboard extends BedwarsScoreboard {
  private static final String BED_EXISTS = "&a\u2714";
  private static final String TEAM_ELIMINATED = "&c\u2718";

  private final GameStageManager gameStageManager;
  private int titlePos;
  private int timePos;
  private final Map<String, Integer> teamsMap = new HashMap<>();
  private final String teamFormat;

  public GameScoreboard(GameContext ctx, GameStageManager gameStageManager) {
    super(ctx);
    this.gameStageManager = gameStageManager;

    this.teamFormat = BedwarsRevol.getInstance().getStringConfig("scoreboard.format-team",
        "$color$&f $team$: $status$");

    Objective health = this.scoreboard.getObjective("health");
    if (health == null) {
      health = this.scoreboard.registerNewObjective("health", "health");
    }
    health.setDisplaySlot(DisplaySlot.BELOW_NAME);
    health.setDisplayName(ChatColor.RED + "❤");
    // Bug SPIGOT-1725: need to force update to show actual health instead of 0
    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      Player player = playerCtx.getPlayer();
      player.setScoreboard(this.scoreboard);
      if (player.getHealth() > 10) {
        player.setHealth(player.getHealth() - 0.01);
      } else {
        player.setHealth(player.getHealth() + 0.01);
      }
    }
  }

  @Override
  public void init() {
    String region = "&fMap: &e" + this.ctx.getRegion().getName();
    this.addLine(region);
    this.addLine("");
    this.titlePos = this.addLine("&b" + this.gameStageManager.getTitle());
    this.timePos = this.addLine("&a" + this.gameStageManager.getTime());
    this.addLine("");

    for (TeamNew team : this.ctx.getTeams().values()) {
      this.teamsMap.put(team.getName(), this.addLine(this.formatTeam(team)));
    }

    this.start();
  }

  @Override
  public void update() {
    this.updateLine(this.titlePos, "&b" + this.gameStageManager.getTitle());
    this.updateLine(this.timePos, "&a" + this.gameStageManager.getTime());

    for (TeamNew team : this.ctx.getTeams().values()) {
      this.updateLine(this.teamsMap.get(team.getName()), this.formatTeam(team));
    }
  }

  private String formatTeam(TeamNew team) {
    String res = this.teamFormat;
    res = res.replace("$color$", team.getChatColor()
        + team.getName().substring(0, 1).toUpperCase());
    res = res.replace("$team$", team.getName());
    String status;
    if (team.isBedDestroyed()) {
      int players = team.getFunctionalPlayers();
      status = players == 0 ? TEAM_ELIMINATED : "&c" + players;
    } else {
      status = BED_EXISTS;
    }
    res = res.replace("$status$", status);
    return res;
  }

}
