package io.github.bedwarsrel.shop;

import io.github.bedwarsrel.shop.upgrades.Upgrade;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class ShopReward {
  private final ItemStack item;
  private final Upgrade upgrade;

  public boolean isUpgrade() {
    return upgrade != null;
  }
}
