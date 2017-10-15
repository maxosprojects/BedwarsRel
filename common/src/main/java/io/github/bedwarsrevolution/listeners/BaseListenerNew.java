package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.event.Listener;

public abstract class BaseListenerNew implements Listener {

  public BaseListenerNew() {
    this.registerEvents();
  }

  private void registerEvents() {
    BedwarsRevol.getInstance().getServer().getPluginManager()
        .registerEvents(this, BedwarsRevol.getInstance());
  }

}
