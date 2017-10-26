package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.utils.UtilsNew;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by {maxos} 2017
 */
public abstract class BedwarsScoreboard {
//  private static final int MAX_SCORE_LENGTH = 40;

  protected final GameContext ctx;
  protected final Scoreboard scoreboard;
  protected Objective display;
  private List<String> lines = new ArrayList<>();

  public BedwarsScoreboard(GameContext ctx) {
    this.ctx = ctx;
    this.scoreboard = this.ctx.getScoreboard();

    this.display = this.scoreboard.getObjective("display");
    if (this.display != null) {
      this.display.unregister();
    }
    this.display = this.scoreboard.registerNewObjective("display", "dummy");
    this.display.setDisplaySlot(DisplaySlot.SIDEBAR);
    this.display.setDisplayName(this.fixLine("&6&lBedWars Revolution"));
  }

  public abstract void init();

  public abstract void update();

  protected int addLine(String line) {
    String fixedLine = this.fixLine(line);
    this.lines.add(fixedLine);
    return this.lines.size() - 1;
  }

  protected void start() {
    List<String> reversedLines = new ArrayList<>(this.lines);
    Collections.reverse(reversedLines);
    ListIterator<String> iter = reversedLines.listIterator();
    while (iter.hasNext()) {
      String line = iter.next();
//    this.scoreboard.resetScores(text);
      Score score = this.display.getScore(line);
      score.setScore(iter.nextIndex());
    }

    for (PlayerContext playerCtx : this.ctx.getPlayers()) {
      playerCtx.getPlayer().setScoreboard(this.scoreboard);
    }
  }

  public void updateLine(int pos, String line) {
    String fixedLine = this.fixLine(line);
    String existing = this.lines.get(pos);
    if (!fixedLine.equals(existing)) {
      this.lines.set(pos, fixedLine);
      this.scoreboard.resetScores(existing);
      this.display.getScore(fixedLine).setScore(this.lines.size() - pos);
    }
  }

  private String fixLine(String line) {
    String fixedLine = ChatColor.translateAlternateColorCodes('&', line);
    fixedLine = UtilsNew.truncate(fixedLine, GameContext.MAX_OBJECTIVE_DISPLAY_LENGTH);
    if (StringUtils.isEmpty(fixedLine)) {
      return StringUtils.repeat(" ", this.lines.size() + 1);
    }
    return fixedLine;
  }

}
