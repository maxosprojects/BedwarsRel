package io.github.bedwarsrevolution.listeners;

import io.github.bedwarsrel.listener.BaseListener;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

public class HangingListenerNew extends BaseListener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onHangingBreak(HangingBreakEvent event) {
    Hanging hanging = event.getEntity();
    if (event.getCause().equals(RemoveCause.OBSTRUCTION)) {
      hanging.getLocation().getBlock().breakNaturally();
      event.setCancelled(true);
    } else if (event.getCause().equals(RemoveCause.EXPLOSION)) {
      event.setCancelled(true);
    }

  }
}
