package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerSpigotListenerNew extends BaseListenerNew {

//  @EventHandler(priority = EventPriority.HIGH)
//  public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
//    if (BedwarsRevol.getInstance().isBungee()) {
//      Player player = event.getPlayer();
//
//      List<GameContext> contexts = BedwarsRevol.getInstance().getGameManager().getGamesContexts();
//      if (contexts.size() == 0) {
//        return;
//      }
//      GameContext firstGame = contexts.get(0);
//      event.setSpawnLocation(firstGame.getPlayerTeleportLocation(player));
//    }
//  }

}
