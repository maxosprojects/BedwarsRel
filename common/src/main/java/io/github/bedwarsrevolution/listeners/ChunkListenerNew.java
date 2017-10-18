package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListenerNew implements Listener {

  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager()
        .getGameByChunkLocation(event.getChunk().getX(),
            event.getChunk().getZ());
    if (ctx == null) {
      return;
    }
    ctx.getState().onEventChunkUnload(event);
  }

}
