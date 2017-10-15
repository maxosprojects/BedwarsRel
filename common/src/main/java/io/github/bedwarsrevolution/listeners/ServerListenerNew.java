package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListenerNew extends BaseListenerNew {

  @EventHandler
  public void onServerListPing(ServerListPingEvent event) {
    // Only enabled on bungeecord
    if (!BedwarsRevol.getInstance().isBungee()) {
      return;
    }
    List<GameContext> contexts = BedwarsRevol.getInstance().getGameManager().getGamesContexts();
    if (BedwarsRevol.getInstance().getGameManager() == null
        || contexts == null
        || contexts.size() == 0) {
      return;
    }
    GameContext ctx = contexts.get(0);
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventServerListPing(event);
  }

}
