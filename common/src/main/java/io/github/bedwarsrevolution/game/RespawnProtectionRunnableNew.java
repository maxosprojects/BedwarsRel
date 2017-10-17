package io.github.bedwarsrevolution.game;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.utils.ChatWriterNew;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnProtectionRunnableNew extends BukkitRunnable {
  private GameContext gameCtx = null;
  private int length = 0;
  private Player player = null;

  public RespawnProtectionRunnableNew(GameContext gameCtx, Player player, int seconds) {
    this.gameCtx = gameCtx;
    this.player = player;
    this.length = seconds;
  }

  @Override
  public void run() {
    if (this.length > 0) {
      this.player
          .sendMessage(ChatWriterNew.pluginMessage(BedwarsRevol._l(player, "ingame.protectionleft",
              ImmutableMap.of("length", String.valueOf(this.length)))));
    }

    if (this.length <= 0) {
      this.player
          .sendMessage(
              ChatWriterNew.pluginMessage(BedwarsRevol._l(this.player, "ingame.protectionend")));
//      this.gameCtx.removeProtection(this.player);
    }

    this.length--;
  }

  public void runProtection() {
    this.runTaskTimerAsynchronously(BedwarsRevol.getInstance(), 5L, 20L);
  }

}
