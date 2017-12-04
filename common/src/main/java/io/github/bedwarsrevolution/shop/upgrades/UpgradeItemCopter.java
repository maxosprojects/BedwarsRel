package io.github.bedwarsrevolution.shop.upgrades;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import io.github.bedwarsrevolution.BedwarsRevol;
import io.github.bedwarsrevolution.game.statemachine.game.GameContext;
import io.github.bedwarsrevolution.game.statemachine.player.PlayerContext;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by {maxos} 2017
 */
public class UpgradeItemCopter extends UpgradeItem {
  protected final String TYPE = "COPTER_ITEM";

  @Override
  public boolean use(PlayerContext playerCtx, ItemStack item, PlayerInteractEvent event) {
    if (item.getType() == Material.DIAMOND_SPADE && item.getDurability() > 1 && item.getDurability() < 6) {
      this.spawnCopter(playerCtx, event.getClickedBlock());
      return true;
    }
    return false;
  }

  private void spawnCopter(PlayerContext playerCtx, Block block) {
    final Player player = playerCtx.getPlayer();
    GameContext ctx = playerCtx.getGameContext();
    // Take one copter from the player
    Inventory inv = player.getInventory();
    int slot = inv.first(Material.DIAMOND_SPADE);
    ItemStack stack = inv.getItem(slot);
    stack.setAmount(stack.getAmount() - 1);
    // Spawn copter
    Location loc = block.getLocation().clone().add(0, 1, 0);
    final ArmorStand holder = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
    holder.setVisible(false);

    ctx.addRunningTask(new BukkitRunnable() {
      private short frame = 2;
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
        if (frame > 5) {
          frame = 2;
        }
      }
    }.runTaskTimer(BedwarsRevol.getInstance(), 0, 1));
  }
}
