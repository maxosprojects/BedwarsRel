package io.github.bedwarsrevolution.shop.upgrades;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * Created by {maxos} 2017
 */
public class UpgradeItemCopter extends UpgradeItem implements Mountable {
  private static final short DURABILITY_REMOTE = 6;
  private static final short DURABILITY_COPTER_START = 2;
  private static final short DURABILITY_COPTER_END = 5;

  protected final String TYPE = "COPTER_ITEM";
  private BukkitTask task;
  private ArmorStand controlPanel;
  private ArmorStand holder;

  @Override
  public boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    if (item.getType() == Material.DIAMOND_SPADE) {
      if (item.getDurability() > 1 && item.getDurability() < 6) {
        this.spawnCopter(playerCtx, event);
        return true;
      } else if (item.getDurability() == 6) {
        return true;
      }
    }
    return false;
  }

  private void spawnCopter(PlayerContext playerCtx, PlayerInteractEvent event) {
    final Player player = playerCtx.getPlayer();
    GameContext ctx = playerCtx.getGameContext();

    // Replace copter in inventory with a remote
    PlayerInventory inv = player.getInventory();
    ItemStack remote = new ItemStack(Material.DIAMOND_SPADE);
    remote.setDurability(DURABILITY_REMOTE);
    if (event.getHand() == EquipmentSlot.HAND) {
      inv.setItemInMainHand(remote);
    } else {
      inv.setItemInOffHand(remote);
    }

//    final ProtocolManager manager = BedwarsRevol.getInstance().getProtocolManager();
//    manager.addPacketListener(
//        new PacketAdapter(BedwarsRevol.getInstance(), ListenerPriority.HIGHEST, Server.ENTITY_DESTROY) {
//      @Override
//      public void onPacketSending(PacketEvent event) {
//        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy(event.getPacket());
//        for (int id : packet.getEntityIDs()) {
//          System.out.println(id);
//        }
//      }
//    });

    // Spawn control panel seat
    final Location loc = player.getLocation().clone().subtract(0, 1, 0);
    this.controlPanel = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
    controlPanel.setVisible(false);
    controlPanel.addPassenger(player);

    // Spawn holder
    final Location inFront = event.getClickedBlock().getLocation().clone().add(0, 1, 0);
    this.holder = (ArmorStand) player.getWorld().spawnEntity(inFront, EntityType.ARMOR_STAND);
    holder.setVisible(false);

//    // Send packet to teleport entity close to the player
//    new BukkitRunnable() {
//      @Override
//      public void run() {
//        WrapperPlayServerEntityTeleport tp = new WrapperPlayServerEntityTeleport();
//        tp.setEntityID(holder.getEntityId());
//        tp.setX(loc.getX());
//        tp.setY(loc.getY());
//        tp.setZ(loc.getZ());
//        tp.sendPacket(player);
//      }
//    }.runTaskLater(BedwarsRevol.getInstance(), 20);

    this.task = new BukkitRunnable() {
      private short frame = DURABILITY_COPTER_START;
      private final ItemStack copter = new ItemStack(Material.DIAMOND_SPADE);

      @Override
      public void run() {
        if (holder.isDead()) {
          cancel();
        }

        copter.setDurability(frame);
        WrapperPlayServerEntityEquipment wrapped = new WrapperPlayServerEntityEquipment();
        wrapped.setEntityID(holder.getEntityId());
        wrapped.setItem(copter);
        wrapped.setSlot(ItemSlot.HEAD);
        wrapped.sendPacket(player);

        frame++;
        if (frame > DURABILITY_COPTER_END) {
          frame = DURABILITY_COPTER_START;
        }
      }
    }.runTaskTimer(BedwarsRevol.getInstance(), 0, 1);
    ctx.addRunningTask(this.task);

    playerCtx.mount(this);
  }

  @Override
  public void unmount(final PlayerContext playerCtx) {
    this.task.cancel();
    new BukkitRunnable() {
      @Override
      public void run() {
        Player player = playerCtx.getPlayer();
        // Remove remote
        PlayerInventory inv = player.getInventory();
        for (ItemStack item : inv.all(Material.DIAMOND_SPADE).values()) {
          if (item.getDurability() == DURABILITY_REMOTE) {
            item.setAmount(0);
          }
        }
        // Remove holder
        holder.remove();
        // Remove control panel
        controlPanel.remove();
      }
    }.runTaskLater(BedwarsRevol.getInstance(), 1);
  }
}
