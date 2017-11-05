package io.github.bedwarsrevolution.shop;

import io.github.bedwarsrevolution.BedwarsRevol;
import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

public class CraftItemStack {
  private Class craftItemStack = null;
  private Object stack = null;

  public CraftItemStack(Object itemStack) {
    this.craftItemStack = BedwarsRevol.getInstance().getCraftBukkitClass("inventory.CraftItemStack");
    this.stack = itemStack;
  }

  @SuppressWarnings("unchecked")
  public ItemStack asBukkitCopy() {
    try {
      Method m =
          this.craftItemStack.getDeclaredMethod("asBukkitCopy", ItemStack.class);
      m.setAccessible(true);
      return (ItemStack) m.invoke(null, this.stack);
    } catch (Exception e) {
//      BedwarsRevol.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Object asNMSCopy() {
    try {
      Method m = this.craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
      m.setAccessible(true);
      return m.invoke(null, this.stack);
    } catch (Exception e) {
//      BedwarsRevol.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }
    return null;
  }
}
