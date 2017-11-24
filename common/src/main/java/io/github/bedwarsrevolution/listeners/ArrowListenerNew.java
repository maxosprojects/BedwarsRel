package io.github.bedwarsrevolution.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Listens on EntityMetadata packet and removes arrows
 */
public class ArrowListenerNew extends BaseListenerNew {
  // For a living entity metadata at index 10 tells "Number of arrows in entity"
  private static final int ARROWS_METADATA_INDEX = 10;

  public ArrowListenerNew registerInterceptor() {
    BedwarsRevol plugin = BedwarsRevol.getInstance();
    plugin.getProtocolManager().addPacketListener(
      new PacketAdapter(plugin, ListenerPriority.HIGHEST, Server.ENTITY_METADATA) {
          @Override
          public void onPacketSending(PacketEvent event) {
            Player player = event.getPlayer();
            WrapperPlayServerEntityMetadata wrapped = new WrapperPlayServerEntityMetadata(event.getPacket());
            Entity entity = wrapped.getEntity(player.getWorld());
            if (entity.getType() == EntityType.PLAYER) {
              List<WrappedWatchableObject> meta = wrapped.getMetadata();
              for (WrappedWatchableObject obj : meta) {
                if (obj.getIndex() == ARROWS_METADATA_INDEX) {
                  obj.setValue(0);
                  break;
                }
              }
            }
          }
        });
    return this;
  }

}
