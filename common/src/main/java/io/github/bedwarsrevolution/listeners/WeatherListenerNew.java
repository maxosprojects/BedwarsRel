package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListenerNew extends BaseListenerNew {

  @EventHandler
  public void onWeatherEvent(WeatherChangeEvent event) {
    if (event.isCancelled()) {
      return;
    }
    List<GameContext> contexts = BedwarsRevol.getInstance().getGameManager()
        .getGamesByWorld(event.getWorld());
    if (contexts.size() == 0) {
      return;
    }
    for (GameContext ctx : contexts) {
      ctx.getState().onEventWeatherChange(event);
    }
  }

}
