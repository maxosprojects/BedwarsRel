package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrel.BedwarsRel;
import org.bukkit.event.Listener;

public abstract class BaseListenerNew implements Listener {

  public BaseListenerNew() {
    this.registerEvents();
  }

  private void registerEvents() {
    BedwarsRel.getInstance().getServer().getPluginManager()
        .registerEvents(this, BedwarsRel.getInstance());
  }

}
