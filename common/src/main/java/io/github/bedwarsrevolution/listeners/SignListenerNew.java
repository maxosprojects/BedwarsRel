package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

public class SignListenerNew extends BaseListenerNew {

  @EventHandler
  public void onSignChange(SignChangeEvent sce) {
    String firstLine = sce.getLine(0).trim();
    if (!"[bw]".equals(firstLine)) {
      return;
    }
    Player player = sce.getPlayer();
    if (!player.hasPermission("bw.setup")) {
      return;
    }
    String gameName = sce.getLine(1).trim();
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameContext(gameName);

    if (ctx == null) {
      String notfound = BedwarsRevol._l("errors.gamenotfoundsimple");
      if (notfound.length() > 16) {
        String[] splitted = notfound.split(" ", 4);
        for (int i = 0; i < splitted.length; i++) {
          sce.setLine(i, ChatColor.RED + splitted[i]);
        }
      } else {
        sce.setLine(0, ChatColor.RED + notfound);
        sce.setLine(1, "");
        sce.setLine(2, "");
        sce.setLine(3, "");
      }

      return;
    }

    sce.setCancelled(true);
    ctx.addJoinSign(sce.getBlock().getLocation());
    ctx.updateSigns();
  }

}
