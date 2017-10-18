package io.github.bedwarsrevolution.game;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class GameJoinSignNew {

  private GameContext gameCtx = null;
  private Location signLocation = null;

  public GameJoinSignNew(GameContext ctx, Location sign) {
    this.gameCtx = ctx;
    this.signLocation = sign;
  }

  private String getCurrentPlayersString() {
    int maxPlayers = this.gameCtx.getMaxPlayers();
    int currentPlayers = this.gameCtx.getPlayers().size();

    String current = "0";
    if (currentPlayers >= maxPlayers) {
      current = ChatColor.RED + String.valueOf(currentPlayers) + ChatColor.WHITE;
    } else {
      current = String.valueOf(currentPlayers);
    }

    return current;
  }

  private String getMaxPlayersString() {
    int maxPlayers = this.gameCtx.getMaxPlayers();
    int currentPlayers = this.gameCtx.getPlayers().size();

    String max = String.valueOf(maxPlayers);

    if (currentPlayers >= maxPlayers) {
      max = ChatColor.RED + max + ChatColor.WHITE;
    }

    return max;
  }

  public Sign getSign() {
    BlockState state = this.signLocation.getBlock().getState();
    if (state instanceof Sign) {
      return (Sign) state;
    }
    return null;
  }

  private String[] getSignLines() {
    String[] sign = new String[4];
    sign[0] = this.replacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("sign.first-line")));
    sign[1] = this.replacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("sign.second-line")));
    sign[2] = this.replacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("sign.third-line")));
    sign[3] = this.replacePlaceholder(ChatColor.translateAlternateColorCodes('&',
        BedwarsRevol.getInstance().getConfig().getString("sign.fourth-line")));

    return sign;
  }

  private String replacePlaceholder(String line) {
    String finalLine = line;

    finalLine = finalLine.replace("$title$", BedwarsRevol._l("sign.firstline"));
    finalLine = finalLine.replace("$gamename$", this.gameCtx.getName());
    finalLine = finalLine.replace("$regionname$", this.gameCtx.getRegion().getName());
    finalLine = finalLine.replace("$maxplayers$", this.getMaxPlayersString());
    finalLine = finalLine.replace("$currentplayers$", this.getCurrentPlayersString());
    finalLine = finalLine.replace("$status$", this.gameCtx.getState().getStatus());

    return finalLine;
  }

  public void updateSign() {
    Sign sign = (Sign) this.signLocation.getBlock().getState();

    String[] signLines = this.getSignLines();
    for (int i = 0; i < signLines.length; i++) {
      sign.setLine(i, signLines[i]);
    }

    sign.update(true, true);
  }

}
