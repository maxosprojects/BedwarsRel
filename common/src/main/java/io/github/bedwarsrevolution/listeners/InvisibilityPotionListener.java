package io.github.bedwarsrevolution.listeners;

import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListener.Parts.CHEST;
import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListener.Parts.FEET;
import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListener.Parts.HEAD;
import static io.github.bedwarsrevolution.listeners.InvisibilityPotionListener.Parts.LEGS;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.listener.BaseListener;
import io.github.bedwarsrel.shop.CraftItemStack;
import io.github.bedwarsrevolution.BedwarsRevol;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

public class InvisibilityPotionListener extends BaseListener {
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
    HEAD;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onConsumeEvent(PlayerItemConsumeEvent event) {
    final Player player = event.getPlayer();
    ItemStack item = event.getItem();
    if (item.getType() == Material.POTION) {
      try {
        Class<?> hiderClass = BedwarsRevol.getInstance().getVersionRelatedClass("HideArmor");
        final Method methodHide = hiderClass.getMethod(
            "hide", int.class, Player.class, int.class);
        final Method methodUnhide = hiderClass.getMethod(
            "unhide", int.class, Player.class, Object.class, int.class);

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
          // Default 30 sec
          int duration = 600;
          for (PotionEffect effect : meta.getCustomEffects()) {
            if (PotionEffectType.INVISIBILITY.equals(effect.getType())) {
              duration = effect.getDuration();
            }
          }
          this.hideArmor(player, methodHide);
          if (invisibilityTasks.containsKey(player)) {
            invisibilityTasks.get(player).cancel();
          }
          invisibilityTasks.put(player, new BukkitRunnable() {
            @Override
            public void run() {
              invisibilityTasks.remove(player);
              try {
                InvisibilityPotionListener.this.unhideArmor(player, methodUnhide);
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
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public InvisibilityPotionListener registerInterceptor() {
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
            InvisibilityPotionListener.this.tableLock.lock();
            boolean invisible = InvisibilityPotionListener.this
                .playerInvisibleTo.containsEntry(invisiblePlayerId, invisibleTo);
            InvisibilityPotionListener.this.tableLock.unlock();
            if (invisible && slot >= 2
                && item != null && item.getAmount() > 0 && item.getType() != Material.AIR) {
              event.setCancelled(true);
            }
          }
        }
      });
    return this;
  }

  private void hideArmor(Player player, Method method)
      throws InvocationTargetException, IllegalAccessException {
    Game game = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    if (game != null && !game.isSpectator(player)) {
      int entityId = player.getEntityId();
      Team playerTeam = game.getPlayerTeam(player);
      for (Team team : game.getTeams().values()) {
        if (team != playerTeam) {
          for (Player playerInOtherTeam : team.getPlayers()) {
            for (Parts part : Parts.values()) {
              method.invoke(null, entityId, playerInOtherTeam, part.ordinal());
              this.tableLock.lock();
              playerInvisibleTo.put(entityId, playerInOtherTeam);
              toInvisiblePlayer.put(playerInOtherTeam, entityId);
              this.tableLock.unlock();
            }
          }
        }
      }
    }
  }

  private void unhideArmor(Player player, Method method)
      throws InvocationTargetException, IllegalAccessException {
    Game game = BedwarsRevol.getInstance().getGameManager().getGameOfPlayer(player);
    boolean makeVisible = player.isOnline() && !game.isSpectator(player);
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
        method.invoke(null, entityId, playerInOtherTeam, feet, FEET.ordinal());
        method.invoke(null, entityId, playerInOtherTeam, legs, LEGS.ordinal());
        method.invoke(null, entityId, playerInOtherTeam, chest, CHEST.ordinal());
        method.invoke(null, entityId, playerInOtherTeam, head, HEAD.ordinal());
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
