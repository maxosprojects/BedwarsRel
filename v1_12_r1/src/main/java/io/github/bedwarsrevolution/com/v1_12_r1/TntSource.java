package io.github.bedwarsrevolution.com.v1_12_r1;

import java.lang.reflect.Field;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityTNTPrimed;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftTNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class TntSource {

  public static void setSource(Player player, TNTPrimed tnt) {
    EntityLiving nmsEntityLiving = (EntityLiving)(((CraftLivingEntity) player).getHandle());
    EntityTNTPrimed nmsTNT = (EntityTNTPrimed) (((CraftTNTPrimed) tnt).getHandle());
    try {
      Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
      sourceField.setAccessible(true);
      sourceField.set(nmsTNT, nmsEntityLiving);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

}
