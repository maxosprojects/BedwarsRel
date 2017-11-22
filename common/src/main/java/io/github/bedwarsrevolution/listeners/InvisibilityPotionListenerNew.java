package io.github.bedwarsrevolution.listeners;

import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListenerNew.Parts.CHEST;
import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListenerNew.Parts.FEET;
import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListenerNew.Parts.HEAD;
import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListenerNew.Parts.LEGS;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.TeamNew;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import io.github.bedwarsrevolution.shop.CraftItemStack;
import io.github.bedwarsrevolution.utils.NmsUtils;
import io.github.bedwarsrevolution.utils.TitleWriterNew;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class InvisibilityPotionListenerNew extends BaseListenerNew {
  private Map<Player, BukkitTask> invisibilityTasks = new HashMap<>();
  // Maps "player that is hidden" to "from which player"
  private Multimap<Integer, Player> playerInvisibleTo = HashMultimap.create();
  // Maps "from which player" to "player that is hidden"
  private Multimap<Player, Integer> toInvisiblePlayer = HashMultimap.create();
  private Lock tableLock = new ReentrantLock();

  enum Parts {
    FEET,
    LEGS,
    CHEST,
    HEAD
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onConsumeEvent(PlayerItemConsumeEvent event) {
    final Player player = event.getPlayer();
    ItemStack item = event.getItem();
    if (item.getType() == Material.POTION) {
      PotionMeta meta = (PotionMeta) item.getItemMeta();
      if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
        // Default 30 sec
        int duration = 600;
        for (PotionEffect effect : meta.getCustomEffects()) {
          if (PotionEffectType.INVISIBILITY.equals(effect.getType())) {
            duration = effect.getDuration();
          }
        }
        this.hideArmor(player);
        if (invisibilityTasks.containsKey(player)) {
          invisibilityTasks.get(player).cancel();
        }
        invisibilityTasks.put(player, new BukkitRunnable() {
          @Override
          public void run() {
            invisibilityTasks.remove(player);
            try {
              InvisibilityPotionListenerNew.this.unhideArmor(player);
              player.sendTitle("", TitleWriterNew.pluginMessage("Invisibility expired"), 10, 70, 20);
            } catch (InvocationTargetException | IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }.runTaskLater(BedwarsRevol.getInstance(), duration));
      }
      new BukkitRunnable() {
        public void run() {
          player.getInventory().remove(Material.GLASS_BOTTLE);
        }
      }.runTaskLater(BedwarsRevol.getInstance(), 1L);
    }
  }

  public InvisibilityPotionListenerNew registerInterceptor() {
    BedwarsRevol plugin = BedwarsRevol.getInstance();
    plugin.getProtocolManager().addPacketListener(
      new PacketAdapter(plugin, ListenerPriority.HIGHEST, Server.ENTITY_EQUIPMENT) {
        @Override
        public void onPacketSending(PacketEvent event) {
          if (event.getPacketType() == Server.ENTITY_EQUIPMENT) {
            Player invisibleTo = event.getPlayer();
            PacketContainer packet = event.getPacket();
            WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(packet);
            int invisiblePlayerId = wrapper.getEntityID();
            int slot = wrapper.getSlot().ordinal();
            ItemStack item = wrapper.getItem();
            InvisibilityPotionListenerNew.this.tableLock.lock();
            boolean invisible = InvisibilityPotionListenerNew.this
                .playerInvisibleTo.containsEntry(invisiblePlayerId, invisibleTo);
            InvisibilityPotionListenerNew.this.tableLock.unlock();
            if (invisible && slot >= 2
                && item != null && item.getAmount() > 0 && item.getType() != Material.AIR) {
              event.setCancelled(true);
            }
          }
        }
      });
    return this;
  }

  private void hideArmor(Player player) {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null || ctx.getPlayerContext(player).getState().isSpectator()) {
      return;
    }
    int entityId = player.getEntityId();
    PlayerContext playerCtx = ctx.getPlayerContext(player);
    TeamNew playerTeam = playerCtx.getTeam();
    for (PlayerContext otherPlayerCtx : ctx.getPlayers()) {
      // Hide from all players on all other teams
      if (otherPlayerCtx.getTeam() != playerTeam) {
        for (Parts part : Parts.values()) {
          Player otherPlayer = otherPlayerCtx.getPlayer();
          NmsUtils.hideArmor(entityId, otherPlayer, part.ordinal());
          this.tableLock.lock();
          playerInvisibleTo.put(entityId, otherPlayer);
          toInvisiblePlayer.put(otherPlayer, entityId);
          this.tableLock.unlock();
        }
      }
    }
  }

  private void unhideArmor(Player player)
      throws InvocationTargetException, IllegalAccessException {
    GameContext ctx = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (ctx == null) {
      return;
    }
    boolean makeVisible = player.isOnline()
        && !ctx.getPlayerContext(player).getState().isSpectator();
    int entityId = player.getEntityId();
    Object feet = getNms(player.getInventory().getBoots());
    Object legs = getNms(player.getInventory().getLeggings());
    Object chest = getNms(player.getInventory().getChestplate());
    Object head = getNms(player.getInventory().getHelmet());

    this.tableLock.lock();
    Collection<Player> removed = playerInvisibleTo.removeAll(entityId);
    for (Player playerInOtherTeam : removed) {
      toInvisiblePlayer.remove(playerInOtherTeam, entityId);
    }
    this.tableLock.unlock();
    for (Player playerInOtherTeam : removed) {
      if (makeVisible) {
        NmsUtils.unhideArmor(entityId, playerInOtherTeam, feet, FEET.ordinal());
        NmsUtils.unhideArmor(entityId, playerInOtherTeam, legs, LEGS.ordinal());
        NmsUtils.unhideArmor(entityId, playerInOtherTeam, chest, CHEST.ordinal());
        NmsUtils.unhideArmor(entityId, playerInOtherTeam, head, HEAD.ordinal());
      }
    }
  }

  private static Object getNms(ItemStack item) {
    if (item != null) {
      return new CraftItemStack(item).asNMSCopy();
    }
    return null;
  }

}
