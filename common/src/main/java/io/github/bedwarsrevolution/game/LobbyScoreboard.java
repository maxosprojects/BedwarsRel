package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

/**
 * Created by {maxos} 2017
 */
public class LobbyScoreboard extends BedwarsScoreboard {
  private static final String BED_EXISTS = "&a\u2714";
  private static final String TEAM_ELIMINATED = "&c\u2718";
  private List<String> lines;

  public LobbyScoreboard(GameContext ctx) {
    super(ctx);
//    obj.setDisplayName(this.formatLine(
//        BedwarsRevol.getInstance().getStringConfig("lobby-scoreboard.title", "&eBedWars Revolution")));
    this.lines = BedwarsRevol.getInstance().getConfig()
        .getStringList("lobby-scoreboard.content");
  }

  @Override
  public void init() {
    for (String line : this.lines) {
      this.addLine(this.formatLine(line));
    }
    this.start();
  }

  @Override
  public void update() {
    int index = 0;
    for (String line : this.lines) {
      if (!StringUtils.isEmpty(line)) {
        this.updateLine(index, this.formatLine(line));
      }
      index++;
    }
    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      playerCtx.getPlayer().setScoreboard(this.scoreboard);
    }
  }

  private String formatLine(String line) {
    String newLine = line;
    newLine = newLine.replace("$regionname$", this.ctx.getRegion().getName());
    newLine = newLine.replace("$gamename$", this.ctx.getName());
    newLine = newLine.replace("$players$", String.valueOf(this.ctx.getPlayers().size()));
    newLine = newLine.replace("$maxplayers$", String.valueOf(this.ctx.getMaxPlayers()));
    return newLine;
  }

}
