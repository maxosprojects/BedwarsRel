package io.github.bedwarsrel.com.v1_12_r1;

import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HideArmor {

  enum Parts {
    FEET(EnumItemSlot.FEET),
    LEGS(EnumItemSlot.LEGS),
    CHEST(EnumItemSlot.CHEST),
    HEAD(EnumItemSlot.HEAD);

    private final EnumItemSlot slot;

    Parts(EnumItemSlot slot) {
      this.slot = slot;
    }

    public static EnumItemSlot getSlot(int num) {
      return Parts.values()[num].slot;
    }
  }

  public static void hide(int entityId, Player invisibleTo, int slot) {
    PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(
        entityId, Parts.getSlot(slot), new ItemStack((Item)null));
    CraftPlayer cp = (CraftPlayer) invisibleTo;
    cp.getHandle().playerConnection.sendPacket(packet);
  }

  public static void unhide(int entityId, Player invisibleTo, Object item, int slot) {
    PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(
        entityId, Parts.getSlot(slot), (ItemStack) item);
    CraftPlayer cp = (CraftPlayer) invisibleTo;

    cp.getHandle().playerConnection.sendPacket(packet);
  }

}
