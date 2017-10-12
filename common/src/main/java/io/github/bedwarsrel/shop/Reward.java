package io.github.bedwarsrel.shop;

import io.github.bedwarsrel.shop.Specials.Upgrade;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class Reward {
  private final ItemStack item;
  private final Upgrade upgrade;

  public boolean isUpgrade() {
    return upgrade != null;
  }
}
