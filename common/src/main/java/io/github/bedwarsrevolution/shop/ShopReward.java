package io.github.bedwarsrevolution.shop;

import io.github.bedwarsrevolution.shop.upgrades.Upgrade;
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
