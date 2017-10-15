package io.github.bedwarsrevolution.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerNamedSoundEffect;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.bedwarsrevolution.BedwarsRevol;
import org.bukkit.Sound;

public class SoundListenerNew extends BaseListenerNew {

  public SoundListenerNew registerInterceptor() {
    BedwarsRevol plugin = BedwarsRevol.getInstance();
    plugin.getProtocolManager().addPacketListener(
      new PacketAdapter(plugin, ListenerPriority.HIGHEST, Server.NAMED_SOUND_EFFECT) {
        @Override
        public void onPacketSending(PacketEvent event) {
          if (event.getPacketType() == Server.NAMED_SOUND_EFFECT) {
            WrapperPlayServerNamedSoundEffect wrapper =
                new WrapperPlayServerNamedSoundEffect(event.getPacket());
            if (wrapper.getSoundEffect() == Sound.ENTITY_ARROW_HIT) {
              event.setCancelled(true);
            }
          }
        }
      });
    return this;
  }

}
